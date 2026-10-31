const Session = {
  // Returns the logged-in user, or sends the browser to the login page.
  async require(role) {
    try {
      const me = await Api.get('/api/auth/me');
      if (role && me.role !== role) { location.href = '/login.html'; return null; }
      return me;
    } catch (e) {
      if (e.status === 401) { location.href = '/login.html'; return null; }
      throw e;
    }
  },
  async logout() {
    await Api.post('/api/auth/logout');
    location.href = '/login.html';
  }
};