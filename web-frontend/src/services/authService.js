const TOKEN_KEY = "token";
const USERNAME_KEY = "userName";

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token);
export const removeToken = () => localStorage.removeItem(TOKEN_KEY);

export const getUserName = () => localStorage.getItem(USERNAME_KEY);
export const setUserName = (name) => localStorage.setItem(USERNAME_KEY, name);
export const removeUserName = () => localStorage.removeItem(USERNAME_KEY);

export const isLoggedIn = () => !!getToken();

export const logout = () => {
  removeToken();
  removeUserName();
}; 