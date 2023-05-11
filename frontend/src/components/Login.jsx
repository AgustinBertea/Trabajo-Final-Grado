import React from "react";
import "../stylesheets/Login.css"
import { useState, useEffect } from "react";
import { Eye, EyeSlash } from "react-bootstrap-icons";
import Axios from "axios";
import { authUserUrl } from "../services/api-rest";
import { useNavigate, Link } from "react-router-dom";

function Login() {

  // Visual
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const smallScreenClasses = "card shadow login-form col-xxl-4 col-xl-5 col-lg-6 col-md-6 col-sm-7 col-12 d-sm-none";
  const otherSizeScreenClasses = "card shadow login-form col-xxl-4 col-xl-5 col-lg-6 col-md-6 col-sm-7 col-12";

  useEffect(() => {
    function handleResize() {
      setIsSmallScreen(window.innerWidth < 576);
    }
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const changeShowPassword = () => {
    setShowPassword(!showPassword);
  }

  // Functionality
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [errorEmailMsg, setErrorEmailMsg] = useState("");
  const [errorPasswordMsg, setErrorPasswordMsg] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/auth/login";
    Axios.post(url, {
      "email": email,
      "password": password
    })
      .then((response) => {
        localStorage.setItem("userId", response.data.id);
        localStorage.setItem("profile", response.data.profile);
        localStorage.setItem("token", "Bearer " + response.data.token);
        localStorage.setItem("email", response.data.email);
        navigate("/home");
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("email")) {
          setErrorEmailMsg("Debe ingresar su correo electrónico.");
        } else if (error.response.data.hasOwnProperty("password")) {
          setErrorPasswordMsg("Debe ingresar su contraseña.");
        } else if (error.response.data.errorMessage === "Error when trying to log in the user, reason: Invalid email or password") {
          setErrorMsg("Los datos ingresados son incorrectos.");
        } else {
          setErrorMsg("Ha ocurrido un error, intente nuevamente en unos minutos.");
        }
      });
  }

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    setErrorMsg("");
    setErrorEmailMsg("");
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    setErrorMsg("");
    setErrorPasswordMsg("");
  };

  const handleRegisterClick = () => {
    navigate("/register");
  }

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
        <h2><b>Ingresa a tu cuenta</b></h2>

        {/* Email */}
        <div className="mb-3">
          <label className="form-label">Ingresa tu correo electrónico</label>
          <input autocomplete="off"
            type="email"
            name="email"
            onChange={handleEmailChange}
            placeholder="correo@mail.com"
            className="form-control" />
        </div>
        {errorEmailMsg.length !== 0 && <div className="alert alert-danger">{errorEmailMsg}</div>}

        {/* Password */}
        <div className="mb-3">
          <label className="form-label">Ingresa tu contraseña</label>
          <div className="input-group">
            <input autocomplete="off"
              type={showPassword ? "text" : "password"}
              name="password"
              onChange={handlePasswordChange}
              placeholder="**********"
              className="form-control" />
            <button
              type="button"
              className="input-group-text"
              onClick={changeShowPassword}> {showPassword ? <EyeSlash /> : <Eye />}
            </button>
          </div>
        </div>
        {errorPasswordMsg.length !== 0 && <div className="alert alert-danger">{errorPasswordMsg}</div>}

        {errorMsg.length !== 0 && <div className="alert alert-danger">{errorMsg}</div>}
        <button type="submit" className="btn btn-warning login-login-button">Ingresar</button>

        <div className="login-forgot-password"><Link to="/forgot-password">¿Olvidaste tu contraseña?</Link></div>

        <div className="mb-3 border-top border"></div>

        <button className="btn btn-dark login-register-Button" onClick={handleRegisterClick}>Registrarse</button>
      </form>
    </div>
  );
}

export default Login;