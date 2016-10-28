package us.bliven.mekoqr;

import static us.bliven.mekoqr.MekoLevel.SIZE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.google.zxing.client.j2se.ImageReader;
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

	public MekoReader() {
		reader = new QRCodeReader();
		hints = new HashMap<>();
		hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	}
	
	
	public MekoLevel readQR(File file) throws NotFoundException, ChecksumException, FormatException, IOException, DataFormatException {
		byte[] raw = readQRraw(file);
		return createLevel(raw);
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
	private byte[] readQRraw(File file) throws NotFoundException, ChecksumException, FormatException, IOException {
		BufferedImage image = ImageReader.readImage(file.toURI());

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
			logger.warn("Using raw data for {}",file);
			byte[] bytes = result.getRawBytes();
			return bytes;
		}
	}
	
	private static MekoLevel createLevel(byte[] raw) throws DataFormatException {
		byte[] b = Arrays.copyOfRange(raw, 4, raw.length);

		// Level consists of two strings (1 + 16 bytes) and the blocks, which can be 1 or 2 bytes
		byte[] uncompressed = new byte[17*2+SIZE*SIZE*SIZE*2];
		int len = MekoReader.inflate(b,b.length,uncompressed);
		
		if(logger.isInfoEnabled()) {
			String hex = MekoReader.bytesToHex(uncompressed);
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
		
		return new MekoLevel(title,author,data);
	}
	
	/**
	 * Uncompress a DEFLATE data stream
	 * @param compressed input compressed data
	 * @param uncompressed output array. Must be large enough
	 * @return length of uncompressed used
	 */
	static int inflate(byte[] compressed, int compressedlen, byte[] uncompressed) {
		return inflate(compressed,0,compressedlen,uncompressed);
	}
	static int inflate(byte[] compressed, int offset, int compressedlen, byte[] uncompressed) {
		try( InflaterInputStream inStream = new InflaterInputStream(new ByteArrayInputStream( compressed ) ) ) {
			int len = offset;
		    int readByte;
		    while((readByte = inStream.read()) != -1) {
		    	uncompressed[len] = (byte)readByte;
		    	len++;
		    }
		    return len; 
		} catch(IOException e) {
			logger.error("Internal error while uncompressing data",e);
			return 0;
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
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		return bytesToHex(bytes,bytes.length);
	}
	public static String bytesToHex(byte[] bytes,int len) {
	    char[] hexChars = new char[len * 3];
	    for ( int j = 0; j < len; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 3] = hexArray[v >>> 4];
	        hexChars[j * 3 + 1] = hexArray[v & 0x0F];
	        hexChars[j * 3 + 2] = ' ';
	    }
	    return new String(hexChars);
	}
	public static void main(String[] args) {
		// List of images to decode
		String[] filenames = new String[] {
//				"/Users/blivens/dev/mekorama/levels/blocks/00_blank.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/01_block_stone.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/02_block_brick.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/03_block_grass.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/04_blank_stone.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/axes.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/all_blocks.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/water1.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/water2.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/water3.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/water4.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/water5.png",
//				"/Users/blivens/dev/mekorama/levels/blocks/pillars.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_Unknown.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_a.png",
				"/Users/blivens/dev/mekorama/levels/title/pristine.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_b.png",
//				"/Users/blivens/dev/mekorama/levels/title/abcd_abcd.png",
//				"/Users/blivens/dev/mekorama/levels/title/b_Unknown.png",
//				"/Users/blivens/dev/mekorama/levels/title/c_Unknown.png",
//				"/Users/blivens/dev/mekorama/levels/title/abcd_a.png",
//				"/Users/blivens/dev/mekorama/levels/title/abcd_b.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_abcd.png",
//				"/Users/blivens/dev/mekorama/levels/title/b_abcd.png",
//				"/Users/blivens/dev/mekorama/levels/title/b_a.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_Unknown_v2.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_Unknown_v3.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_aa.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_aaa.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_aaaa.png",
//				"/Users/blivens/dev/mekorama/levels/title/a_aaaaaaaaaaaaaaaa.png",
//				"/Users/blivens/dev/mekorama/levels/real/my_level_2.jpg",
//				"/Users/blivens/dev/mekorama/levels/title/a-p_a-p.png",
//				"/Users/blivens/dev/mekorama/levels/real/Praying_For_Rain_nGord.png",
//				"/Users/blivens/dev/mekorama/levels/pos/stone_000.png",
//				"/Users/blivens/dev/mekorama/levels/pos/stone_100.png",
//				"/Users/blivens/dev/mekorama/levels/pos/stone_010.png",
//				"/Users/blivens/dev/mekorama/levels/pos/stone_001.png",
//				"/Users/blivens/dev/mekorama/levels/gen/00_blank_regen.png",
//				"/Users/blivens/dev/mekorama/levels/gen/sequence.png",
		};
		
		MekoReader qr = new MekoReader();
		
		for( String filename : filenames) {
			try {
				File f = new File(filename);
				if(!f.exists()) {
					System.err.format("File not found: %s%n",filename);
					continue;
				}
				// Decode QR code
				MekoLevel level = qr.readQR(f);
				
				// Print level info
				System.out.println(filename);
				//System.out.println(bytesToHex(level.getRawData()));
				System.out.println(level.summarize());
				//System.out.format("Length: %d\t%d\t%d%n",bytes.length,bytes[0],bytes.length-bytes[0]);
				
//				// Save data to a file
//				File outFile = new File(f.getParentFile(),f.getName()+".lvl");
//				try( FileOutputStream out = new FileOutputStream(outFile) ) {
//					//out.write(new byte[]{0x1f,(byte) 0x8b}); //gzip magic
//					out.write(level.getRawData());
//				}
			} catch ( ChecksumException | FormatException
					| IOException |DataFormatException e) {
				logger.error("Error reading {}",filename,e);
				System.err.format("Error reading %s%n",filename);
			} catch (NotFoundException e) {
				logger.error("No QR found in {}",filename,e);
				System.err.format("No QR found in %s%n",filename);
			}
		}
	}
	
}
