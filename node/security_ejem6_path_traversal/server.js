const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const app = express();

const IMAGES_FOLDER = path.join(process.cwd(), 'images');
if (!fs.existsSync(IMAGES_FOLDER)) fs.mkdirSync(IMAGES_FOLDER, { recursive: true });

app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');
app.use(express.static(__dirname + '/public'));

// busboy (used internally by multer) runs the multipart "filename" through
// basename() by default, which would quietly block traversal here. Passing
// preservePath: true disables that and hands us the filename exactly as the
// client sent it in the Content-Disposition header - so intercepting the
// upload request with a proxy and rewriting filename="../../../../tmp/x.txt"
// escapes the images directory on write, mirroring the Spring original
// (IMAGES_FOLDER.resolve(image.getOriginalFilename()), no sanitization at all).
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, IMAGES_FOLDER),
  filename: (req, file, cb) => cb(null, file.originalname),
});
const upload = multer({ storage, preservePath: true });

app.post('/upload_image', upload.single('image'), (req, res) => {
  res.render('uploaded_image', { imageName: req.file.originalname });
});

app.get('/image', (req, res) => {
  res.render('view_image', { imageName: req.query.imageName });
});

// Vulnerable: imageName is resolved against IMAGES_FOLDER with no
// sanitization/containment check, so "../../../../etc/passwd" escapes the
// images directory (classic path traversal).
app.get('/download_image', (req, res) => {
  const imagePath = path.join(IMAGES_FOLDER, req.query.imageName);
  fs.readFile(imagePath, (err, data) => {
    if (err) return res.status(404).send('Not found');
    res.set('Content-Type', 'image/jpeg');
    res.send(data);
  });
});

const PORT = process.env.PORT || 9000;
app.listen(PORT, () => {
  console.log(`Path Traversal example listening on http://localhost:${PORT}`);
});
