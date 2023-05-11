import React from "react";
import "../stylesheets/ForgotPassword.css"
import Axios from "axios";
import { authUserUrl } from "../services/api-rest";
import { Link } from "react-router-dom";
import { Eye, EyeSlash } from "react-bootstrap-icons";
import { useState, useEffect } from "react";

function ForgotPassword() {

  // Visual
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [isPasswordChanged, setIsPasswordChanged] = useState(false);
  const smallScreenClasses = "card shadow forgot-password-form col-xxl-4 col-xl-5 col-lg-6 col-md-6 col-sm-7 col-12 d-sm-none";
  const otherSizeScreenClasses = "card shadow forgot-password-form col-xxl-4 col-xl-5 col-lg-6 col-md-8 col-sm-11";
  const nonChangedPasswordClasses = "forgot-password-container";
  const changedPasswordClasses = "forgot-password-container hide";

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
  const [newPassword, setNewPassword] = useState("");
  const [secretQuestion, setSecretQuestion] = useState("");
  const [secretAnswer, setSecretAnswer] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [errorEmailMsg, setErrorEmailMsg] = useState("");
  const [errorNewPasswordMsg, setErrorNewPasswordMsg] = useState("");
  const [errorSecretQuestionMsg, setErrorSecretQuestionMsg] = useState("");
  const [errorSecretAnswerMsg, setErrorSecretAnswerMsg] = useState("");

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    setErrorMsg("");
    setErrorEmailMsg("");
  };

  const handleNewPasswordChange = (e) => {
    setNewPassword(e.target.value);
    setErrorMsg("");
    setErrorNewPasswordMsg("");
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

  const handleSubmit = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/recovery";
    Axios.put(url, {
      "email": email,
      "newPassword": newPassword,
      "secretQuestion": secretQuestion,
      "secretAnswer": secretAnswer
    })
      .then((response) => {
        setIsPasswordChanged(true);
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("email")) {
          if (error.response.data.email === "Invalid email address") {
            setErrorEmailMsg("Debe ingresar un correo electrónico válido.");
          } else {
            setErrorEmailMsg("Debe ingresar su correo electrónico.");
          }
        } else if (error.response.data.hasOwnProperty("newPassword")) {
          setErrorNewPasswordMsg("La nueva contraseña debe tener 8 caracteres como mínimo.");
        } else if (error.response.data.hasOwnProperty("secretQuestion")) {
          setErrorSecretQuestionMsg("Debe ingresar su pregunta secreta.");
        } else if (error.response.data.hasOwnProperty("secretAnswer")) {
          setErrorSecretAnswerMsg("Debe ingresar su respuesta secreta.");
        } else {
          setErrorMsg("Ha ocurrido un error, intente nuevamente en unos minutos.");
        }
      });
  }

  return (
    <>
      <div className={isPasswordChanged ? changedPasswordClasses : nonChangedPasswordClasses}>
        <form onSubmit={handleSubmit} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>

          <h2><b>Crea una nueva contraseña</b></h2>
          <div className="alert alert-warning forgot-password-remembered">
            ¿Has recordado tu contraseña?&nbsp;<Link to="/login">¡Ingresa aquí!</Link>
          </div>

          {/* Email */}
          <div className="mb-3">
            <label className="form-label">Ingresa tu correo electrónico</label>
            <input autocomplete="off"
              type="text"
              name="email"
              onChange={handleEmailChange}
              placeholder="correo@mail.com"
              className="form-control" />
          </div>
          {errorEmailMsg.length !== 0 && <div className="alert alert-danger">{errorEmailMsg}</div>}

          {/* Secret question */}
          <div className="mb-3">
            <label className="form-label">Ingresa tu pregunta secreta</label>
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
            <label className="form-label">Ingresa tu respuesta secreta</label>
            <input autocomplete="off"
              type="text"
              name="answer"
              onChange={handleSecretAnswerChange}
              placeholder="Lionel Messi"
              className="form-control" />
          </div>
          {errorSecretAnswerMsg.length !== 0 && <div className="alert alert-danger">{errorSecretAnswerMsg}</div>}

          {/* New password */}
          <div className="mb-3">
            <label className="form-label">Ingresa la nueva contraseña</label>
            <div className="input-group">
              <input autocomplete="off"
                type={showPassword ? "text" : "password"}
                name="password"
                onChange={handleNewPasswordChange}
                placeholder="**********"
                className="form-control" />
              <button
                type="button"
                className="input-group-text"
                onClick={changeShowPassword}> {showPassword ? <EyeSlash /> : <Eye />}
              </button>
            </div>
          </div>
          {errorNewPasswordMsg.length !== 0 && <div className="alert alert-danger">{errorNewPasswordMsg}</div>}

          {errorMsg.length !== 0 && <div className="alert alert-danger">{errorMsg}</div>}
          <button type="submit" className="btn btn-warning forgot-password-change-password-button">Crear nueva contraseña</button>
        </form>
      </div>



      <div className={isPasswordChanged ? nonChangedPasswordClasses : changedPasswordClasses}>
        <div className={isSmallScreen ? smallScreenClasses + " changed" : otherSizeScreenClasses + " changed"}>
          <h2><b>Has cambiado tu contraseña con éxito</b></h2>
          <b><Link to="/login">¡Ingresa aquí con tu nueva contraseña!</Link></b>
        </div>
      </div>
    </>
  );
}

export default ForgotPassword;