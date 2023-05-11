import React from "react";
import "../stylesheets/Register.css"
import Axios from "axios";
import { authUserUrl } from "../services/api-rest";
import { Link } from "react-router-dom";
import { Eye, EyeSlash } from "react-bootstrap-icons";
import { useState, useEffect } from "react";

function Register() {

  // Visual
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [isRegistered, setIsRegistered] = useState(false);
  const smallScreenClasses = "card shadow register-form col-xxl-4 col-xl-5 col-lg-6 col-md-6 col-sm-7 col-12 d-sm-none";
  const otherSizeScreenClasses = "card shadow register-form col-xxl-4 col-xl-5 col-lg-6 col-md-8 col-sm-11";
  const nonRegisteredUserClasses = "register-container";
  const registeredUserClasses = "register-container hide";

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
  const [secretQuestion, setSecretQuestion] = useState("");
  const [secretAnswer, setSecretAnswer] = useState("");
  const [profile, setProfile] = useState("Sin inconvenientes");
  const [errorMsg, setErrorMsg] = useState("");
  const [errorEmailMsg, setErrorEmailMsg] = useState("");
  const [errorPasswordMsg, setErrorPasswordMsg] = useState("");
  const [errorSecretQuestionMsg, setErrorSecretQuestionMsg] = useState("");
  const [errorSecretAnswerMsg, setErrorSecretAnswerMsg] = useState("");

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

  const handleSecretQuestionChange = (e) => {
    setSecretQuestion(e.target.value);
    setErrorMsg("");
    setErrorSecretQuestionMsg("");
  };

  const handleSecretAnswerChange = (e) => {
    setSecretAnswer(e.target.value);
    setErrorMsg("");
    setErrorSecretAnswerMsg("");
  };

  const handleProfileChange = (e) => {
    setProfile(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    Axios.post(authUserUrl, {
      "email": email,
      "password": password,
      "secretQuestion": secretQuestion,
      "secretAnswer": secretAnswer,
      "profile": profile
    })
      .then((response) => {
        setIsRegistered(true);
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("email")) {
          if (error.response.data.email === "Invalid email address") {
            setErrorEmailMsg("Debe ingresar un correo electrónico válido.");
          } else {
            setErrorEmailMsg("Debe ingresar un correo electrónico.");
          }
        } else if (error.response.data.hasOwnProperty("password")) {
          setErrorPasswordMsg("La contraseña debe tener 8 caracteres como mínimo.");
        } else if (error.response.data.hasOwnProperty("secretQuestion")) {
          setErrorSecretQuestionMsg("Debe ingresar una pregunta secreta.");
        } else if (error.response.data.hasOwnProperty("secretAnswer")) {
          setErrorSecretAnswerMsg("Debe ingresar una respuesta secreta.");
        } else if (error.response.data.errorMessage === "The email is already taken") {
          setErrorEmailMsg("La dirección de correo electrónico ya se encuentra registrada.");
        } else {
          setErrorMsg("Ha ocurrido un error, intente nuevamente en unos minutos.");
        }
      });
  }

  return (
    <>
      <div className={isRegistered ? registeredUserClasses : nonRegisteredUserClasses}>
        <form onSubmit={handleSubmit} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <h2><b>Crea tu cuenta</b></h2>
          <div className="alert alert-warning register-have-account">
            ¿Ya tienes una cuenta?&nbsp;<Link to="/login">¡Ingresa aquí!</Link>
          </div>

          {/* Email */}
          <div className="mb-3">
            <label className="form-label">Ingresa un correo electrónico</label>
            <input autocomplete="off"
              type="text"
              name="email"
              onChange={handleEmailChange}
              placeholder="correo@mail.com"
              className="form-control" />
          </div>
          {errorEmailMsg.length !== 0 && <div className="alert alert-danger">{errorEmailMsg}</div>}

          {/* Password */}
          <div className="mb-3">
            <label className="form-label">Ingresa una contraseña</label>
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

          {/* Secret question */}
          <div className="mb-3">
            <label className="form-label">Ingresa una pregunta secreta</label>
            <input autocomplete="off"
              type="text"
              name="question"
              onChange={handleSecretQuestionChange}
              placeholder="¿Mejor jugador del mundo?"
              className="form-control" />
          </div>
          {errorSecretQuestionMsg.length !== 0 && <div className="alert alert-danger">{errorSecretQuestionMsg}</div>}

          {/* Secret answer */}
          <div className="mb-3">
            <label className="form-label">Ingresa una respuesta secreta</label>
            <input autocomplete="off"
              type="text"
              name="answer"
              onChange={handleSecretAnswerChange}
              placeholder="Lionel Messi"
              className="form-control" />
          </div>
          {errorSecretAnswerMsg.length !== 0 && <div className="alert alert-danger">{errorSecretAnswerMsg}</div>}

          {/* Profile */}
          <div className="mb-3">
            <label className="form-label">Elige el perfil que mejor se adapte a tu situación</label>
            <select className="form-select" onChange={handleProfileChange}>
              <option value="Sin inconvenientes">Sin inconvenientes</option>
              <option value="Inconvenientes de movilidad">Inconvenientes de movilidad</option>
              <option value="Inconvenientes de visión">Inconvenientes de visión</option>
            </select>
          </div>

          {errorMsg.length !== 0 && <div className="alert alert-danger">{errorMsg}</div>}
          <button type="submit" className="btn btn-warning register-register-button">Registrarse</button>
        </form>
      </div>



      <div className={isRegistered ? nonRegisteredUserClasses : registeredUserClasses}>
        <div className={isSmallScreen ? smallScreenClasses + " registered" : otherSizeScreenClasses + " registered"}>
          <h2><b>Te has registrado con éxito</b></h2>
          <b><Link to="/login">¡Ingresa aquí con tu nueva cuenta!</Link></b>
        </div>
      </div>
    </>
  );
}

export default Register;