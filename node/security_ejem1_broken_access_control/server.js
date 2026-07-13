const express = require('express');
const session = require('express-session');
const bcrypt = require('bcryptjs');
const {
  users,
  posts,
  findUserByName,
  findUserById,
  findPostById,
  createPost,
  deletePost,
} = require('./data');

const app = express();

app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');

app.use(express.urlencoded({ extended: true }));
app.use(express.static(__dirname + '/public'));
app.use(
  session({
    secret: 'dev-secret-not-for-production',
    resave: false,
    saveUninitialized: false,
  })
);

// Equivalent of the Spring @ModelAttribute that exposes login info to every view.
app.use((req, res, next) => {
  const user = req.session.user ? findUserById(req.session.user.id) : null;
  res.locals.logged = !!user;
  res.locals.userName = user ? user.name : null;
  res.locals.admin = !!(user && user.roles.includes('ADMIN'));
  req.currentUser = user;
  next();
});

// Any logged-in user may reach these routes - there is no per-object ownership
// check here, which is the vulnerability we are reproducing (IDOR / broken
// access control), matching the Spring hasAnyRole("USER") config.
function requireLogin(req, res, next) {
  if (!req.currentUser) return res.redirect('/login');
  next();
}

app.get('/', (req, res) => {
  res.render('post/list', { posts });
});

app.get('/posts/:id', (req, res) => {
  const post = findPostById(req.params.id);
  if (!post) return res.status(404).send('Post not found');
  const author = findUserById(post.authorId);
  // isOwner only gates whether the Edit/Remove buttons are shown in the
  // view - it is NOT enforced again on the actual edit/remove endpoints.
  const isOwner =
    !!req.currentUser &&
    (req.currentUser.id === post.authorId || req.currentUser.roles.includes('ADMIN'));
  res.render('post/view', { post: { ...post, author }, isOwner });
});

app.get('/newpost', requireLogin, (req, res) => {
  res.render('post/new');
});

app.post('/newpost', requireLogin, (req, res) => {
  const { title, text } = req.body;
  const post = createPost({ title, text, authorId: req.currentUser.id });
  res.render('post/created-message', { postId: post.id });
});

app.get('/editpost/:id', requireLogin, (req, res) => {
  const post = findPostById(req.params.id);
  if (!post) return res.status(404).send('Post not found');
  // No ownership check - any logged-in user can open the edit form for any post.
  res.render('post/edit', { post });
});

app.post('/editpost', requireLogin, (req, res) => {
  const { id, title, text } = req.body;
  const post = findPostById(id);
  if (!post) return res.status(404).send('Post not found');
  // No ownership check here either - this is the actual vulnerable endpoint.
  post.title = title;
  post.text = text;
  res.render('post/edited-message', { postId: post.id });
});

app.post('/removepost/:id', requireLogin, (req, res) => {
  const post = findPostById(req.params.id);
  if (!post) return res.status(404).send('Post not found');
  // No ownership check - any logged-in user can delete any post.
  deletePost(post.id);
  res.render('post/removed-message', { title: post.title });
});

app.get('/login', (req, res) => {
  res.render('session/login');
});

app.post('/login', (req, res) => {
  const { username, password } = req.body;
  const user = findUserByName(username);
  if (user && bcrypt.compareSync(password, user.passwordHash)) {
    req.session.user = { id: user.id };
    return res.redirect('/');
  }
  res.redirect('/loginerror');
});

app.get('/loginerror', (req, res) => {
  res.render('session/loginerror');
});

app.post('/logout', (req, res) => {
  req.session.destroy(() => res.redirect('/'));
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Broken Access Control example listening on http://localhost:${PORT}`);
  console.log('Users: michel/pass, oscar/pass, admin/adminpass');
});
