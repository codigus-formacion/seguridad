const bcrypt = require('bcryptjs');

const users = [
  { id: 1, name: 'michel', passwordHash: bcrypt.hashSync('pass', 8), roles: ['USER'] },
  { id: 2, name: 'oscar', passwordHash: bcrypt.hashSync('pass', 8), roles: ['USER'] },
  { id: 3, name: 'admin', passwordHash: bcrypt.hashSync('adminpass', 8), roles: ['USER', 'ADMIN'] },
];

const posts = [
  { id: 1, title: 'First post', text: 'This is the content of the first post', authorId: 1 },
  { id: 2, title: 'Second post', text: 'This is the content of the second post', authorId: 2 },
  { id: 3, title: 'Third post', text: 'This is the content of the third post', authorId: 1 },
];

let nextPostId = 4;

function findUserByName(name) {
  return users.find((u) => u.name === name);
}

function findUserById(id) {
  return users.find((u) => u.id === id);
}

function findPostById(id) {
  return posts.find((p) => p.id === Number(id));
}

function createPost({ title, text, authorId }) {
  const post = { id: nextPostId++, title, text, authorId };
  posts.push(post);
  return post;
}

function deletePost(id) {
  const idx = posts.findIndex((p) => p.id === Number(id));
  if (idx !== -1) posts.splice(idx, 1);
}

module.exports = { users, posts, findUserByName, findUserById, findPostById, createPost, deletePost };
