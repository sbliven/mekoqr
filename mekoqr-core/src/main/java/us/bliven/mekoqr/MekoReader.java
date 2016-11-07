package us.bliven.mekoqr;

import static us.bliven.mekoqr.MekoLevel.SIZE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.bliven.mekoqr.json.JsonTransformer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * Class to read in Mekorama level QR codes
 * @author blivens
 *
 */
public class MekoReader {
	private static final Logger logger = LoggerFactory.getLogger(MekoReader.class);
	private QRCodeReader reader;
	private Map<DecodeHintType, Object> hints;
	private boolean storeRaw;
	private boolean storeData;

	public MekoReader() {
		this(false,false);
	}
	
	public MekoReader(boolean storeRaw, boolean storeData) {
		this.storeRaw = storeRaw;
		this.storeData = storeData;
		reader = new QRCodeReader();
		hints = new HashMap<>();
		hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	}


	public MekoLevel readQR(File file) throws NotFoundException, ChecksumException, FormatException, IOException, DataFormatException {
		byte[] raw = readQRraw(file);
		MekoLevel level = createLevel(raw);
		if(storeRaw) {
			level.setRawData(raw);
		}
		return level;
	}
	public MekoLevel readQR(InputStream is) throws NotFoundException, ChecksumException, FormatException, IOException, DataFormatException {
		byte[] raw = readQRraw(is);
		MekoLevel level = createLevel(raw);
		if(storeRaw) {
			level.setRawData(raw);
		}
		return level;

	}
	
	/**
	 * Read binary data from a QR code.
	 * @param file Image containing the QR code
	 * @return binary data
	 * @throws NotFoundException QR code not detected in the image
	 * @throws ChecksumException Too many QR read errors
	 * @throws FormatException Illegal QR code or miss-detected code
	 * @throws IOException Error reading image file
	 */
	private byte[] readQRraw(BufferedImage image) throws NotFoundException, ChecksumException, FormatException, IOException {
		// creating binary bitmap from source image
	    LuminanceSource lumSource = new BufferedImageLuminanceSource(image);
	    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(lumSource));

		Result result = reader.decode(bitmap,hints);
		
//		logger.info("Length: {}",result.getNumBits()/8.);
//		logger.info("Meta: {}",result.getResultMetadata());
		if(result.getResultMetadata().containsKey(ResultMetadataType.BYTE_SEGMENTS)) {
			// Use processed byte segments
			// Strips initial length & trailing padding
			@SuppressWarnings("unchecked")
			List<byte[]> segments = (List<byte[]>) result.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS);
			// Single segment
			if( segments.size() == 1) {
				return segments.get(0);
			}
			// Join multiple segments
			int len = segments.stream()
					.mapToInt((seg) -> seg.length)
					.sum();
			byte[] allseg = new byte[len];
			int pos=0;
			//logger.info("Segments:");
			for(byte[] seg : segments) {
				//logger.info("({})\t{}",String.format("%02x",seg.length),bytesToHex(seg));
				System.arraycopy(seg, 0, allseg, pos, seg.length);
				pos+=seg.length;
			}
			assert pos == len;
			return allseg;
		} else {
			// Fall back on raw data
			// warning may throw off byte alignment (typically starts with 3-nibble length var)
			logger.warn("Using raw data");
			byte[] bytes = result.getRawBytes();
			return bytes;
		}
	}
	private byte[] readQRraw(File file) throws NotFoundException, ChecksumException, FormatException, IOException {
		BufferedImage image = ImageIO.read(file);
		return readQRraw(image);
	}
	private byte[] readQRraw(InputStream is) throws NotFoundException, ChecksumException, FormatException, IOException {
		BufferedImage image = ImageIO.read(is);
		return readQRraw(image);
	}
	
	private MekoLevel createLevel(byte[] raw) throws DataFormatException, ZipException, IOException {
		byte[] b = Arrays.copyOfRange(raw, 4, raw.length);

		// Level consists of two strings (1 + 16 bytes) and the blocks, which can be 1 or 2 bytes
		byte[] uncompressed = new byte[17*2+SIZE*SIZE*SIZE*2];
		int len = MekoReader.inflate(b,b.length,uncompressed);
		
		if(logger.isInfoEnabled()) {
			String hex = Utils.bytesToHex(uncompressed);
			logger.info("Decompressed {} bytes starting with {}{}",len, hex.substring(0, Math.min(len, 30)),len>30?"...":"");
		}

		// Parse data
		
		int pos = 0;
		
		// Title
		int titleLen = uncompressed[pos];
		pos += 1;
		String title;
		try {
			title = new String(uncompressed,pos,titleLen,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DataFormatException(String.format("Malformed title (%d bytes)",titleLen));
		}
		pos += titleLen;
		
		// Author
		int authorLen = uncompressed[pos];
		pos += 1;
		String author;
		try {
			author = new String(uncompressed,pos,authorLen,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DataFormatException(String.format("Malformed author (%d bytes)",authorLen));
		}
		pos += authorLen;
		
		BlockType[] data = new BlockType[SIZE*SIZE*SIZE];
		pos += parseData(uncompressed, pos, len, data);
		
		if(pos != len) {
			logger.error("{} bytes were not parsed: {}",len-pos,Arrays.copyOfRange(uncompressed, pos, len));
		}
		
		MekoLevel level = new MekoLevel(title,author,data);
		if(storeData) {
			level.setSerializedData(Arrays.copyOfRange(uncompressed, 0, len));
		}
		return level;
	}
	
	/**
	 * Uncompress a DEFLATE data stream
	 * @param compressed input compressed data
	 * @param uncompressed output array. Must be large enough
	 * @return length of uncompressed used
	 * @throws IOException 
	 * @throws ZipException 
	 */
	static int inflate(byte[] compressed, int compressedlen, byte[] uncompressed) throws ZipException, IOException {
		return inflate(compressed,0,compressedlen,uncompressed);
	}
	static int inflate(byte[] compressed, int offset, int compressedlen, byte[] uncompressed) throws ZipException, IOException {
		try( InflaterInputStream inStream = new InflaterInputStream(new ByteArrayInputStream( compressed ) ) ) {
			int len = offset;
		    int readByte;
		    while((readByte = inStream.read()) != -1) {
		    	uncompressed[len] = (byte)readByte;
		    	len++;
		    }
		    return len; 
		}
	}

	static int inflateWrapped(byte[] compressed, int compressedlen,
			byte[] uncompressed) throws DataFormatException {
		Inflater decompresser = new Inflater(false);
		decompresser.setInput(compressed, 0, compressedlen);
		int uncompressedlen = decompresser.inflate(uncompressed);
		return uncompressedlen;
	}

	/**
	 * Parses the main data 
	 * @param level 
	 * @return Number of bytes parsed
	 */
	private static int parseData(byte[] data, int start, int end, BlockType[] level) {
		assert level.length == SIZE*SIZE*SIZE;
		int pos = 0;
		
		int i;
		for(i=start;i<end && pos < level.length;i++) {
			byte val = data[i];
			BlockType blk = BlockType.fromByte(val);
			if( blk.hasSubtypes() ) {
				i++;
				val = data[i];
				try {
					blk = blk.getSubtype(val);
				} catch(IllegalArgumentException e) {
					logger.error(e.getMessage());
				}
			}
			level[pos] = blk;
			pos++;
		}
		return i-start;
	}
	

	/**
	 * CLI options
	 * @return
	 */
	private static Options createOptions() {
		return new Options()
		.addOption("h", false, "help")
		.addOption("a", "ascii", true, "Output ascii-art level")
		.addOption("o", "output", true, "Output level summary")
		.addOption("j","json",true, "Output json description")
		.addOption(Option.builder("r")
				.longOpt("raw")
				.optionalArg(true)
				.numberOfArgs(1)
				.argName("file")
				.desc( "Write raw QR data. If empty, include raw data in json")
				.build() )
		.addOption(Option.builder("d")
				.longOpt("data")
				.optionalArg(true)
				.numberOfArgs(1)
				.argName("file")
				.desc( "Write level data. If empty, include raw data in json")
				.build() )
		;
	}
	public static void main(String[] args) {
		
		// Parse options
		String usage = "[OPTIONS] qrfiles";
		CommandLineParser parser = new DefaultParser();
		Options options = createOptions();
		HelpFormatter help = new HelpFormatter();
		CommandLine cmd;
		try {
			cmd = parser.parse( options, args, false);
			args = cmd.getArgs();
		} catch (ParseException e1) {
			help.printHelp(usage, options);
			System.exit(2);
			return;
		}
				
		// help
		if( cmd.hasOption("h")) {
			help.printHelp(usage, options);
			System.exit(0);
			return;
		}
		// parsing params
		boolean storeRaw = cmd.hasOption("r");
		boolean storeData = cmd.hasOption("d");
		
		// Input file
		if( args.length != 1 ) {
			System.err.println("No QR file specified");
			help.printHelp(usage, options);
			System.exit(2);
			return;
		}
		String filename = args[0];
		
		MekoReader qr = new MekoReader(storeRaw, storeData);
		
		try {
			File f = new File(filename);
			if(!f.exists()) {
				System.err.format("File not found: %s%n",filename);
				System.exit(1);
				return;
			}
			// Decode QR code
			MekoLevel level = qr.readQR(f);

			// Print level info
			if( cmd.hasOption("o")) {
				Utils.writeFile(cmd.getOptionValue("o"), level.summarize());
			}
			
			boolean json = cmd.hasOption("j");
			
			if( cmd.hasOption("r")) {
				String out = cmd.getOptionValue("r");
				if( json && !out.isEmpty() ) {
					Utils.writeBytes(out, level.getRawData());
				}
			}
			if( cmd.hasOption("d")) {
				String out = cmd.getOptionValue("d");
				if( json && !out.isEmpty() ) {
					Utils.writeBytes(out, level.getSerializedData());
				}
			}
			if( cmd.hasOption("j")) {
				JsonTransformer jsonT = new JsonTransformer();
				String jstr = jsonT.render(level);
				Utils.writeFile(cmd.getOptionValue("j"), jstr);
			}
			if( cmd.hasOption("o")) {
				String ascii = level.summarize();
				Utils.writeFile(cmd.getOptionValue("o"), ascii);
			}
			if( cmd.hasOption("a")) {
				String ascii = level.ascii();
				Utils.writeFile(cmd.getOptionValue("a"), ascii);
			}
		} catch ( ChecksumException | FormatException
				| IOException |DataFormatException e) {
			logger.error("Error reading {}",filename,e);
			System.err.format("Error reading %s%n",filename);
			System.exit(1); return;
		} catch (NotFoundException e) {
			logger.error("No QR found in {}",filename,e);
			System.err.format("No QR found in %s%n",filename);
			System.exit(1); return;
		}
	}
	
}
