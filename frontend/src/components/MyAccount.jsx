import React from "react";
import "../stylesheets/MyAccount.css"
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { Eye, EyeSlash } from "react-bootstrap-icons";
import { authUserUrl } from "../services/api-rest";
import Axios from "axios";

function MyAccount() {

  // Visual
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const [isChangingSomething, setIsChangingSomething] = useState(false);
  const [isChangingProfile, setIsChangingProfile] = useState(false);
  const [isProfileChanged, setIsProfileChanged] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [isPasswordChanged, setIsPasswordChanged] = useState(false);
  const [isChangingSecretQA, setIsChangingSecretQA] = useState(false);
  const [isSecretQAChanged, setIsSecretQAChanged] = useState(false);
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);
  const [areYouSure, setAreYouSure] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showDeletePassword, setShowDeletePassword] = useState(false);
  const smallScreenClasses = "card shadow my-account-div col-xxl-4 col-xl-5 col-lg-6 col-md-6 col-sm-7 col-12 d-sm-none";
  const otherSizeScreenClasses = "card shadow my-account-div col-xxl-4 col-xl-5 col-lg-6 col-md-8 col-sm-11";

  useEffect(() => {
    if (localStorage.getItem("profile") === "Inconvenientes de visión") {
      navigate("/home");
    }
  }, []);

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

  const changeShowNewPassword = () => {
    setShowNewPassword(!showNewPassword);
  }

  const changeShowCurrentPassword = () => {
    setShowCurrentPassword(!showCurrentPassword);
  }

  const changeShowDeletePassword = () => {
    setShowDeletePassword(!showDeletePassword);
  }

  const handleChangingProfile = () => {
    setIsChangingSomething(!isChangingSomething);
    setIsChangingProfile(!isChangingProfile);
    setErrorMsg("");
    setIsProfileChanged(false);
    setIsPasswordChanged(false);
    setIsSecretQAChanged(false);
  }

  const handleChangingPassword = () => {
    setIsChangingSomething(!isChangingSomething);
    setIsChangingPassword(!isChangingPassword);
    setErrorMsg("");
    setErrorPasswordMsg("");
    setErrorNewPasswordMsg("");
    setIsProfileChanged(false);
    setIsPasswordChanged(false);
    setIsSecretQAChanged(false);
    setPassword("");
    setNewPassword("");
    setShowPassword(false);
    setShowNewPassword(false);
  }

  const handleChangingSecretQA = () => {
    setIsChangingSomething(!isChangingSomething);
    setIsChangingSecretQA(!isChangingSecretQA);
    setErrorMsg("");
    setErrorCurrentPasswordMsg("");
    setErrorQuestionMsg("");
    setErrorAnswerMsg("");
    setIsProfileChanged(false);
    setIsPasswordChanged(false);
    setIsSecretQAChanged(false);
    setShowCurrentPassword(false);
    setCurrentPassword("");
    setSecretQuestion("");
    setSecretAnswer("");
  }

  const handleDeletingAccount = () => {
    setIsChangingSomething(!isChangingSomething);
    setIsDeletingAccount(!isDeletingAccount);
    setAreYouSure(false);
    setErrorMsg("");
    setIsProfileChanged(false);
    setIsPasswordChanged(false);
    setIsSecretQAChanged(false);
    setShowDeletePassword(false);
    setDeletePassword("");
    setDeleteSecretQuestion("");
    setDeleteSecretAnswer("");
    setErrorDeletePasswordMsg("");
    setErrorDeleteQuestionMsg("");
    setErrorDeleteAnswerMsg("");
  }

  const handleAreYouSure = () => {
    setAreYouSure(true);
    setErrorMsg("");
    setIsProfileChanged(false);
    setIsPasswordChanged(false);
    setIsSecretQAChanged(false);
  }

  // Functionality
  const navigate = useNavigate();
  const [profile, setProfile] = useState(localStorage.getItem("profile"));
  const [password, setPassword] = useState("");
  const [errorPasswordMsg, setErrorPasswordMsg] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [errorNewPasswordMsg, setErrorNewPasswordMsg] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [errorCurrentPasswordMsg, setErrorCurrentPasswordMsg] = useState("");
  const [deletePassword, setDeletePassword] = useState("");
  const [errorDeletePasswordMsg, setErrorDeletePasswordMsg] = useState("");
  const [secretQuestion, setSecretQuestion] = useState("");
  const [errorQuestionMsg, setErrorQuestionMsg] = useState("");
  const [deleteSecretQuestion, setDeleteSecretQuestion] = useState("");
  const [errorDeleteQuestionMsg, setErrorDeleteQuestionMsg] = useState("");
  const [secretAnswer, setSecretAnswer] = useState("");
  const [errorAnswerMsg, setErrorAnswerMsg] = useState("");
  const [deleteSecretAnswer, setDeleteSecretAnswer] = useState("");
  const [errorDeleteAnswerMsg, setErrorDeleteAnswerMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const handleClickBack = () => {
    navigate("/home");
    setErrorMsg("");
  };

  const handleProfileChange = (e) => {
    setProfile(e.target.value);
    setErrorMsg("");
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    setErrorMsg("");
    setErrorPasswordMsg("");
  };

  const handleNewPasswordChange = (e) => {
    setNewPassword(e.target.value);
    setErrorMsg("");
    setErrorNewPasswordMsg("");
  };

  const handleCurrentPasswordChange = (e) => {
    setCurrentPassword(e.target.value);
    setErrorMsg("");
    setErrorCurrentPasswordMsg("");
  };

  const handleDeletePasswordChange = (e) => {
    setDeletePassword(e.target.value);
    setErrorMsg("");
    setErrorDeletePasswordMsg("");
  };

  const handleSecretQuestionChange = (e) => {
    setSecretQuestion(e.target.value);
    setErrorMsg("");
    setErrorQuestionMsg("");
  };

  const handleDeleteSecretQuestionChange = (e) => {
    setDeleteSecretQuestion(e.target.value);
    setErrorMsg("");
    setErrorDeleteQuestionMsg("");
  };

  const handleSecretAnswerChange = (e) => {
    setSecretAnswer(e.target.value);
    setErrorMsg("");
    setErrorAnswerMsg("");
  };

  const handleDeleteSecretAnswerChange = (e) => {
    setDeleteSecretAnswer(e.target.value);
    setErrorMsg("");
    setErrorDeleteAnswerMsg("");
  };

  const handleClickDisconnect = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleSubmitProfile = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/" + localStorage.getItem("userId") + "/profile";
    Axios.put(url, profile,
      {
        headers: {
          Authorization: localStorage.getItem("token"),
          "Content-Type": "text/plain"
        }
      })
      .then((response) => {
        localStorage.setItem("profile", profile);
        setIsChangingSomething(!isChangingSomething);
        setIsChangingProfile(!isChangingProfile);
        setErrorMsg("");
        setIsProfileChanged(true);
      })
      .catch((error) => {
        setErrorMsg("Ha ocurrido un error, intente nuevamente en unos minutos.");
      });
  };

  const handleSubmitPassword = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/" + localStorage.getItem("userId") + "/password";
    Axios.put(url, {
      "currentPassword": password,
      "newPassword": newPassword
    },
      {
        headers: {
          Authorization: localStorage.getItem("token")
        }
      })
      .then((response) => {
        setIsChangingSomething(!isChangingSomething);
        setIsChangingPassword(!isChangingPassword);
        setErrorMsg("");
        setIsPasswordChanged(true);
        setPassword("");
        setNewPassword("");
        setShowPassword(false);
        setShowNewPassword(false);
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("currentPassword")) {
          setErrorPasswordMsg("Debe ingresar su contraseña");
        } else if (error.response.data.hasOwnProperty("newPassword")) {
          setErrorNewPasswordMsg("La nueva contraseña debe tener 8 caracteres como mínimo.");
        } else {
          setErrorMsg("Los datos ingresados son incorrectos.");
        }
      });
  };

  const handleSubmitSecretQA = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/" + localStorage.getItem("userId") + "/question-answer";
    Axios.put(url, {
      "password": currentPassword,
      "secretQuestion": secretQuestion,
      "secretAnswer": secretAnswer
    },
      {
        headers: {
          Authorization: localStorage.getItem("token")
        }
      })
      .then((response) => {
        setIsChangingSomething(!isChangingSomething);
        setIsChangingSecretQA(!isChangingSecretQA);
        setErrorMsg("");
        setIsSecretQAChanged(true);
        setShowCurrentPassword(false);
        setCurrentPassword("");
        setSecretQuestion("");
        setSecretAnswer("");
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("password")) {
          setErrorCurrentPasswordMsg("Debe ingresar su contraseña.");
        } else if (error.response.data.hasOwnProperty("secretQuestion")) {
          setErrorQuestionMsg("Debe ingresar una pregunta secreta.");
        } else if (error.response.data.hasOwnProperty("secretAnswer")) {
          setErrorAnswerMsg("Debe ingresar una respuesta secreta.");
        } else {
          setErrorMsg("Los datos ingresados son incorrectos.");
        }
      });
  };

  const handleSubmitDeleteAccount = (e) => {
    e.preventDefault();
    const url = authUserUrl + "/" + localStorage.getItem("userId");
    Axios.delete(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      },
      data: {
        "password": deletePassword,
        "secretQuestion": deleteSecretQuestion,
        "secretAnswer": deleteSecretAnswer
      }
    })
      .then((response) => {
        localStorage.clear();
        navigate("/login");
      })
      .catch((error) => {
        if (error.response.data.hasOwnProperty("password")) {
          setErrorDeletePasswordMsg("Debe ingresar su contraseña.");
        } else if (error.response.data.hasOwnProperty("secretQuestion")) {
          setErrorDeleteQuestionMsg("Debe ingresar su pregunta secreta.");
        } else if (error.response.data.hasOwnProperty("secretAnswer")) {
          setErrorDeleteAnswerMsg("Debe ingresar su respuesta secreta.");
        } else {
          setErrorMsg("Los datos ingresados son incorrectos.");
        }
      });
  };

  return (
    <>
      <div className={isChangingSomething ? "my-account-container hide" : "my-account-container"}>
        <div className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <div className="row align-items-center">
            <div className="col">
              <h2><b>Mi cuenta</b></h2>
            </div>
            <div className="col-auto">
              <button type="button" onClick={handleClickBack} className="btn btn-dark">Volver</button>
            </div>
          </div>

          {/* Email */}
          <div className="mb-3">
            <label className="form-label">Correo electrónico</label>
            <input autocomplete="off"
              type="text"
              placeholder={localStorage.getItem("email")}
              className="form-control" disabled />
          </div>

          {/* Profile */}
          <div className="mb-3">
            <label className="form-label">Perfil</label>
            <div className="row align-items-center">
              <div className="col">
                <input autocomplete="off"
                  type="text"
                  placeholder={localStorage.getItem("profile")}
                  className="form-control" disabled />
              </div>
              <div className="col col-auto">
                <button type="button" onClick={handleChangingProfile} className="btn my-account-button btn-warning ">Cambiar</button>
              </div>
            </div>
          </div>
          {isProfileChanged && <div className="alert alert-warning my-account-changed">
            ¡Perfil correctamente actualizado!
          </div>}


          {/* Password */}
          <div className="mb-3">
            <label className="form-label">Contraseña</label>
            <div className="row align-items-center">
              <div className="col">
                <input autocomplete="off"
                  type="password"
                  placeholder="**********"
                  className="form-control" disabled />
              </div>
              <div className="col col-auto">
                <button type="button" onClick={handleChangingPassword} className="btn my-account-button btn-warning ">Cambiar</button>
              </div>
            </div>
          </div>
          {isPasswordChanged && <div className="alert alert-warning my-account-changed">
            ¡Contraseña correctamente actualizada!
          </div>}

          {/* Secret question and answer*/}
          <div className="mb-3">
            <label className="form-label">Pregunta y respuesta secreta</label>
            <div className="row align-items-center">
              <div className="col">
                <input autocomplete="off"
                  type="password"
                  placeholder="**********"
                  className="form-control" disabled />
              </div>
              <div className="col">
                <input autocomplete="off"
                  type="password"
                  placeholder="**********"
                  className="form-control" disabled />
              </div>
              <div className="col col-auto">
                <button type="button" onClick={handleChangingSecretQA} className="btn my-account-button btn-warning ">Cambiar</button>
              </div>
            </div>
          </div>
          {isSecretQAChanged && <div className="alert alert-warning my-account-changed">
            ¡Pregunta y respuseta secreta correctamente actualizadas!
          </div>}

          <div className="mb-3 border-top border mt-3"></div>
          <button type="button" className="btn btn-dark mt-3 col-12" onClick={handleClickDisconnect}> Cerrar sesión </button>
          <button type="button" onClick={handleDeletingAccount} className="btn btn-danger mt-3 col-12">Eliminar mi cuenta</button>
        </div>
      </div>




      {/* Changing profile */}
      <div className={isChangingProfile ? "my-account-container" : "my-account-container hide"}>
        <form onSubmit={handleSubmitProfile} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <div className="row align-items-center">
            <div className="col">
              <h2><b>Cambio de perfil</b></h2>
            </div>
            <div className="col-auto">
              <button type="button" onClick={handleChangingProfile} className="btn btn-danger">Cancelar</button>
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label mt-3">Elige el perfil que mejor se adapte a tu situación</label>
            <select defaultValue={localStorage.getItem("profile")} className="form-select mt-2" onChange={handleProfileChange}>
              <option value="Sin inconvenientes">Sin inconvenientes</option>
              <option value="Inconvenientes de movilidad">Inconvenientes de movilidad</option>
              <option value="Inconvenientes de visión">Inconvenientes de visión</option>
            </select>
            {errorMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorMsg}</div>}
            <button type="submit" className="btn btn-warning mt-4 col-12">Cambiar perfil</button>
          </div>
        </form>
      </div>

      {/* Changing password */}
      <div className={isChangingPassword ? "my-account-container" : "my-account-container hide"}>
        <form onSubmit={handleSubmitPassword} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <div className="row align-items-center">
            <div className="col">
              <h2><b>Cambio de contraseña</b></h2>
            </div>
            <div className="col-auto">
              <button type="button" onClick={handleChangingPassword} className="btn btn-danger">Cancelar</button>
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label mt-3">Ingresa tu contraseña actual</label>
            <div className="input-group">
              <input autocomplete="off"
                type={showPassword ? "text" : "password"}
                name="password"
                value={password}
                onChange={handlePasswordChange}
                placeholder="**********"
                className="form-control" />
              <button
                type="button"
                className="input-group-text"
                onClick={changeShowPassword}> {showPassword ? <EyeSlash /> : <Eye />}
              </button>
            </div>
            {errorPasswordMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorPasswordMsg}</div>}
            <label className="form-label mt-3">Ingresa tu nueva contraseña</label>
            <div className="input-group">
              <input autocomplete="off"
                type={showNewPassword ? "text" : "password"}
                name="newPassword"
                value={newPassword}
                onChange={handleNewPasswordChange}
                placeholder="**********"
                className="form-control" />
              <button
                type="button"
                className="input-group-text"
                onClick={changeShowNewPassword}> {showNewPassword ? <EyeSlash /> : <Eye />}
              </button>
            </div>
            {errorNewPasswordMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorNewPasswordMsg}</div>}
            {errorMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorMsg}</div>}
            <button className="btn btn-warning mt-4 col-12">Cambiar contraseña</button>
          </div>
        </form>
      </div>

      {/* Changing secret question-answer */}
      <div className={isChangingSecretQA ? "my-account-container" : "my-account-container hide"}>
        <form onSubmit={handleSubmitSecretQA} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <div className="row align-items-center">
            <div className="col">
              <h2><b>Cambio de pregunta y respuesta secreta</b></h2>
            </div>
            <div className="col-auto">
              <button type="button" onClick={handleChangingSecretQA} className="btn btn-danger">Cancelar</button>
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label mt-3">Ingresa tu contraseña actual</label>
            <div className="input-group">
              <input autocomplete="off"
                type={showCurrentPassword ? "text" : "password"}
                name="currentPassword"
                value={currentPassword}
                onChange={handleCurrentPasswordChange}
                placeholder="**********"
                className="form-control" />
              <button
                type="button"
                className="input-group-text"
                onClick={changeShowCurrentPassword}> {showCurrentPassword ? <EyeSlash /> : <Eye />}
              </button>
            </div>
            {errorCurrentPasswordMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorCurrentPasswordMsg}</div>}
            <label className="form-label mt-3">Ingresa tu nueva pregunta secreta</label>
            <div className="input-group">
              <input autocomplete="off"
                type="text"
                name="question"
                value={secretQuestion}
                onChange={handleSecretQuestionChange}
                placeholder="¿Mejor jugador del mundo?"
                className="form-control" />
            </div>
            {errorQuestionMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorQuestionMsg}</div>}
            <label className="form-label mt-3">Ingresa tu nueva respuesta secreta</label>
            <div className="mb-3">
              <input autocomplete="off"
                type="text"
                name="answer"
                value={secretAnswer}
                onChange={handleSecretAnswerChange}
                placeholder="Lionel Messi"
                className="form-control" />
            </div>
            {errorAnswerMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorAnswerMsg}</div>}
            {errorMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorMsg}</div>}
            <button type="submit" className="btn btn-warning mt-4 col-12">Cambiar pregunta y respuesta secreta</button>
          </div>
        </form>
      </div>

      {/* Deleting account */}
      <div className={isDeletingAccount ? "my-account-container" : "my-account-container hide"}>
        <form onSubmit={handleSubmitDeleteAccount} className={isSmallScreen ? smallScreenClasses : otherSizeScreenClasses}>
          <div className="row align-items-center">
            <div className="col">
              <h2><b>Eliminar mi cuenta</b></h2>
            </div>
            <div className="col-auto">
              <button type="button" onClick={handleDeletingAccount} className="btn btn-dark">Cancelar</button>
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label mt-3">Ingresa tu contraseña actual</label>
            <div className="input-group">
              <input autocomplete="off"
                type={showDeletePassword ? "text" : "password"}
                name="deletePassword"
                value={deletePassword}
                onChange={handleDeletePasswordChange}
                placeholder="**********"
                className="form-control" />
              <button
                type="button"
                className="input-group-text"
                onClick={changeShowDeletePassword}> {showDeletePassword ? <EyeSlash /> : <Eye />}
              </button>
            </div>
            {errorDeletePasswordMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorDeletePasswordMsg}</div>}
            <label className="form-label mt-3">Ingresa tu pregunta secreta</label>
            <div className="input-group">
              <input autocomplete="off"
                type="text"
                name="deleteSecretQuestion"
                value={deleteSecretQuestion}
                onChange={handleDeleteSecretQuestionChange}
                placeholder="¿Mejor jugador del mundo?"
                className="form-control" />
            </div>
            {errorDeleteQuestionMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorDeleteQuestionMsg}</div>}
            <label className="form-label mt-3">Ingresa tu respuesta secreta</label>
            <div className="mb-3">
              <input autocomplete="off"
                type="text"
                name="deleteSecretAnswer"
                value={deleteSecretAnswer}
                onChange={handleDeleteSecretAnswerChange}
                placeholder="Lionel Messi"
                className="form-control" />
            </div>
            {errorDeleteAnswerMsg.length !== 0 && <div className="alert alert-danger mt-3 mb-0">{errorDeleteAnswerMsg}</div>}
            <button type="button" onClick={handleAreYouSure} className="btn btn-danger mt-4 col-12">Quiero eliminar mi cuenta</button>
          </div>
          {areYouSure && <div className="alert alert-danger container-fluid text-center">
            <div className="row"><div className="col"><label>Esta acción no se puede deshacer...</label></div></div>
            <div className="row"><div className="col mt-1"><label>¿Esta seguro que quiere eliminar su cuenta?</label> </div></div>
            <div className="row"><div className="col mt-3"><button type="submit" className="btn btn-dark">Sí, quiero eliminar mi cuenta</button> </div></div>
          </div>}
          {errorMsg.length !== 0 && <div className="alert alert-danger mt-0 mb-3 mb-0">{errorMsg}</div>}
        </form>
      </div>
    </>
  );
}

export default MyAccount;