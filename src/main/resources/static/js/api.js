// Small wrapper around fetch(): adds the CSRF header and turns errors into exceptions.
const Api = (() => {
  function getCookie(name) {
    const part = document.cookie.split('; ').find(r => r.startsWith(name + '='));
    return part ? decodeURIComponent(part.split('=')[1]) : null;
  }

  async function request(method, url, body) {
    const headers = { 'Accept': 'application/json' };
    if (method !== 'GET') {
      if (!getCookie('XSRF-TOKEN')) await fetch('/api/auth/csrf', { credentials: 'same-origin' });
      headers['X-XSRF-TOKEN'] = getCookie('XSRF-TOKEN');
    }
    let payload;
    if (body instanceof URLSearchParams) {
      payload = body;                                   // form login
    } else if (body !== undefined) {
      headers['Content-Type'] = 'application/json';
      payload = JSON.stringify(body);
    }
    const res = await fetch(url, { method, headers, body: payload, credentials: 'same-origin' });
    const text = await res.text();
    let data = null;
    if (text) { try { data = JSON.parse(text); } catch (e) { /* not JSON */ } }
    if (!res.ok) {
      const err = new Error((data && data.message) || ('HTTP ' + res.status));
      err.status = res.status;
      err.data = data;
      throw err;
    }
    return data;
  }

  return {
    get: url => request('GET', url),
    post: (url, body) => request('POST', url, body),
  };
})();