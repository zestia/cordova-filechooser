(function() {
  var FileChooser = {
    pickFile: function(success, error) {
      cordova.exec(
        success,
        error,
        "FileChooser",
        "open",
        []
      );
    },
  };

  module.exports = FileChooser;
})();
