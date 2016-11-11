/* After selecting a file, add that image to the specified div
 */
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

function uploadFile(url,file,$div,rotation){
  var xhr = new XMLHttpRequest();
  xhr.responseType = "blob";
  xhr.open("POST", url, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      // Every thing ok, file uploaded
      // get the image back
      var blob = xhr.response;
      // base64 encode the blob
      var urlCreator = window.URL || window.webkitURL;
      var imageUrl = urlCreator.createObjectURL(blob);

      var $img = $("<img/>", {
        class: 'img-responsive',
        src: imageUrl
      });

      $div.empty().append($img);

    } else {
      var $err = $("<pre></pre>")
        .addClass("alert alert-danger");
      if(xhr.response) {
        $err.text(xhr.response);
      } else {
        $err.text("Error rotating level");
      }
      $div.empty().append($err);
    }
  };
  var fd = new FormData();
  fd.append("uploaded_file", file);
  xhr.send(fd);
}
