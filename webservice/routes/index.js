var express = require('express');
var router = express.Router();
var multer  = require('multer') // https://github.com/expressjs/multer
var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/')
  },
  filename: function (req, file, cb) {
    cb(null, file.fieldname + '-' + Date.now() + '.jpg')
  }
})
// var upload = multer({ dest: 'uploads/' })
var upload = multer({ storage: storage })

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/ping', function(req, res, next) {
  res.send({ title: 'Express', content: 'Express content', count: 3 });
});


var cpUpload = upload.fields([
  { name: 'image', maxCount: 1 },
  { name: 'square1', maxCount: 1 },
  { name: 'square2', maxCount: 1 },
  { name: 'square3', maxCount: 1 },
  { name: 'square4', maxCount: 1 }]);
router.post('/file-upload', cpUpload, function(req, res, next) {
    console.log(req.body);
    console.log(req.file);
    res.render('index', { title: 'Upload done' });
});

module.exports = router;
