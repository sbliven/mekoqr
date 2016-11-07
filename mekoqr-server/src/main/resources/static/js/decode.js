
function previewImage(file,$div) {
  var imageType = /image.*/;

  if (!file.type.match(imageType)) {
    throw "File Type must be an image";
  }

  var $img = $("<img/>", {
    class: 'img-responsive'});

  $div.empty().append($img);

  // Using FileReader to display the image content
  var reader = new FileReader();
  reader.onload = (function(aImg) {
    return function(e) { aImg.attr('src', e.target.result); };
  })($img);
  reader.readAsDataURL(file);
}

function uploadFile(file,$infoDiv, $tableDiv){
  var url = '/json';
  var xhr = new XMLHttpRequest();
  var fd = new FormData();
  xhr.open("POST", url, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      // Every thing ok, file uploaded
      //console.log(xhr.responseText); // handle response.
      var levelJson = xhr.responseText;
      var level = JSON.parse(levelJson);
      displayLevel(level,$infoDiv, $tableDiv);

      var shortenLongStrings = function(k,v) {
        if (typeof v === "string" && v.length > 30) {
          return v.substring(0,30) + "...";
        }
        return v;
      }
      var $pre = $("<pre></pre>")
        .addClass("pre-scrollable")
        .text(JSON.stringify(level, null, 2));
      $("#json").append($pre);
    } else {
      var $err = $("<pre></pre>")
        .addClass("alert alert-danger")
        .text(xhr.responseText);
      $infoDiv.empty().append($err);
    }
  };
  fd.append("uploaded_file", file);
  xhr.send(fd);
}

function mkCollapsable(id, header, contents) {
  var $button = $("<button></button>", {
    "data-toggle": "collapse",
    "data-target": "#"+id
  }).text(header);

  // var $scroller = $("<pre></pre>")
  //   .addClass("pre-scrollable")
  //   .text(contents);
  // var $innerDiv = $("<div></div>").addClass("panel-body").append($scroller);
  var $innerDiv = $("<div></div>").addClass("panel-body").text(contents);
  var $contentDiv = $("<div></div>", {
    "id": id,
    "class": "collapse panel panel-default"
  }).append($innerDiv);

  return $("<div></div>").append($button, $contentDiv);
}
function displayLevel(level, $infoDiv, $tableDiv) {
  var $title = $("<h3></h3>").text(level.title);
  var $author = $("<h4></h4>").text(level.author);

  var $compressedDiv = mkCollapsable("compressedData","Show QR code data",level.rawData);
  var $uncompressedDiv = mkCollapsable("uncompressedData","Show uncompressed data",level.serializedData);
  $infoDiv.empty().append($title,$author,$compressedDiv,$uncompressedDiv);

  var $tbody = $tableDiv.find("tbody");
  level.data.sort(function(a,b) {
    // Sort by y, then z then x
    if(a.y != b.y) {
      return a.y-b.y;
    }
    if(a.z != b.z) {
      return a.z-b.z;
    }
    if(a.x != b.x) {
      return a.x-b.x;
    }
    return 0; // Shouldn't happen
  });
  level.data.forEach(function(blk) {
    var $row = $("<tr></tr>")
    .append( $("<td></td>").text(blk.x) )
    .append( $("<td></td>").text(blk.y) )
    .append( $("<td></td>").text(blk.z) )
    .append( $("<td></td>").text(blk.type.name) );

    if( blk.type.parent ) {
      // Has orientation
      $row.append( $("<td></td>").text(blk.type.parent.value) );
      $row.append( $("<td></td>").text(blk.type.value) );
    } else {
      $row.append( $("<td></td>").text(blk.type.value) );
      $row.append( $("<td></td>") );
    }

    $tbody.append($row);
  });
}
/*
var uploadfiles = document.querySelector('#fileinput');
uploadfiles.addEventListener('change', function () {
var files = this.files;
for(var i=0; i<files.length; i++){
previewImage(this.files[i]);
uploadFile(this.files[i]); // call the function to upload the file
}

}, false);
*/
