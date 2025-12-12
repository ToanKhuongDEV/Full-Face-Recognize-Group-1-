import api, { setAccessToken } from "./http";

const LOGIN_PATH = "/api/v1/auth/login";

export async function login(username, password) {
  const { data } = await api.post(LOGIN_PATH, { username, password });
  if (data.status === "SUCCESS" && data.data) {
    const token = data.data;
    sessionStorage.setItem("accessToken", token);
    setAccessToken(token);
  }
  return data;
}
