import React from "react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../stylesheets/Vision.css";
import { Mic, MicFill } from "react-bootstrap-icons";
import { useSpeechSynthesis, useSpeechRecognition } from "react-speech-kit";
import { authUserUrl } from "../services/api-rest";
import { graphUrl } from "../services/api-rest";
import Axios from "axios";

function Vision() {
  const navigate = useNavigate();
  const [voice, setVoice] = useState();
  const [listening, setListening] = useState(false);
  const [transcript, setTranscript] = useState("batata");
  const [changingProfile, setChangingProfile] = useState(false);
  const [calculatingPath, setCalculatingPath] = useState(false);
  const [streetStart, setStreetStart] = useState("");
  const [numberStart, setNumberStart] = useState(-1);
  const [streetFinish, setStreetFinish] = useState("");
  const [numberFinish, setNumberFinish] = useState(-1);
  const [pathInstructions, setPathInstructions] = useState([]);
  const [instructionNumber, setInstructionNumber] = useState(-1);
  const [onPath, setOnPath] = useState(false);
  const [evenNodes, setEvenNodes] = useState([]);
  const [oddNodes, setOddNodes] = useState([]);
  const [pathNodes, setPathNodes] = useState([]);
  const [locationLatitude, setLocationLatitude] = useState(-31.4133);
  const [locationLongitude, setLocationLongitude] = useState(-64.1840);


  const { speak, cancel } = useSpeechSynthesis();
  const { listen, stop } = useSpeechRecognition({
    onResult: (result) => {
      setTranscript(result);
    },
  });

  function capitalize(str) {
    return str.toLowerCase().replace(/(?:^|\s)\S/g, function (firstLetter) {
      return firstLetter.toUpperCase();
    }).replace(/\b(De|Del?|La?|Los?|Y)(\s)/gi, function (matched, article, space) {
      return article.toLowerCase() + space;
    });
  }

  useEffect(() => {
    if (pathNodes.length !== 0) {
      const latitudeGap = Math.abs(Math.abs(pathNodes[instructionNumber].latitude) - Math.abs(locationLatitude));
      const longitudeGap = Math.abs(Math.abs(pathNodes[instructionNumber].longitude) - Math.abs(locationLongitude));
      if (latitudeGap < 0.0001 & longitudeGap < 0.0001) {
        handleNextInstruction();
      }
    }
  }, [locationLatitude, locationLongitude]);

  const handleNextInstruction = () => {
    if (pathInstructions[instructionNumber + 1] === "SKIP") {
      setInstructionNumber(instructionNumber + 2);
    } else {
      setInstructionNumber(instructionNumber + 1);
    }
  };

  useEffect(() => {
    if (instructionNumber !== -1 && (pathInstructions.length) - 1 === instructionNumber) {
      speak({ text: pathInstructions[instructionNumber] + " QUE TENGAS UN EXCELENTE DÍA", voice: voice, rate: 0.7, pitch: 0.5 });
      setCalculatingPath(false);
      setStreetStart("");
      setStreetFinish("");
      setNumberStart(-1);
      setNumberFinish(-1);
      setPathInstructions([]);
      setInstructionNumber(-1);
      setOnPath(false);
    } else if (instructionNumber !== -1) {
      speak({ text: pathInstructions[instructionNumber], voice: voice, rate: 0.7, pitch: 0.5 });
    }
  }, [instructionNumber]);

  useEffect(() => {
    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setLocationLatitude(latitude);
        setLocationLongitude(longitude);
      },
      (error) => {
        console.error(error);
      }
    );

    return () => {
      navigator.geolocation.clearWatch(watchId);
    };
  }, []);

  useEffect(() => {
    if (listening === false && calculatingPath === true && streetStart === "") {
      switch (transcript) {
        case "cancelar":
          speak({
            text: "CANCELANDO EL CÁLCULO DE LA RUTA.", voice: voice, rate: 0.7, pitch: 0.5
          });
          setCalculatingPath(false);
          setStreetStart("");
          setStreetFinish("");
          setNumberStart(-1);
          setNumberFinish(-1);
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          const url = graphUrl + "/graph/nodes-all-street";
          Axios.get(url, {
            headers: {
              Authorization: localStorage.getItem('token'),
              'Content-Type': 'application/json'
            },
            params: {
              street: capitalize(transcript),
            }
          })
            .then((response) => {
              if (response.data.length !== 0) {
                setEvenNodes(response.data.filter(node => node.number % 2 === 0));
                setOddNodes(response.data.filter(node => node.number % 2 !== 0));
                setStreetStart(capitalize(transcript));
                speak({ text: "AHORA DIME EL NÚMERO DE " + transcript + " DESDE DONDE COMENZARÁS, DE LO CONTRARIO DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
              } else {
                speak({ text: "LA CALLE " + transcript + " NO ESTÁ CONTEMPLADA EN EL SISTEMA POR EL MOMENTO. INTENTA CON OTRA CALLE O DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
              }
            })
            .catch((error) => {
              speak({ text: "HA OCURRIDO UN ERROR, INTENTA DE NUEVO EN UNOS MINUTOS.", voice: voice, rate: 0.7, pitch: 0.5 });
              setCalculatingPath(false);
              setStreetStart("");
              setStreetFinish("");
              setNumberStart(-1);
              setNumberFinish(-1);
            });
      }
    }

    if (listening === false && calculatingPath === true && streetStart !== "" && numberStart === -1) {
      switch (transcript) {
        case "cancelar":
          speak({
            text: "CANCELANDO EL CÁLCULO DE LA RUTA.", voice: voice, rate: 0.7, pitch: 0.5
          });
          setCalculatingPath(false);
          setStreetStart("");
          setStreetFinish("");
          setNumberStart(-1);
          setNumberFinish(-1);
          setEvenNodes([]);
          setOddNodes([]);
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          if (!isNaN(transcript)) {
            let closestNode;
            if (transcript % 2 === 0) {
              const index = evenNodes.findIndex(node => node.number >= transcript);
              if (index === 0) {
                closestNode = evenNodes[0];
              } else {
                const prevNode = evenNodes[index - 1];
                const currNode = evenNodes[index];
                if (transcript - prevNode.number < currNode.number - transcript) {
                  closestNode = prevNode;
                } else {
                  closestNode = currNode;
                }
              }
            } else {
              const index = oddNodes.findIndex(node => node.number >= transcript);
              if (index === 0) {
                closestNode = oddNodes[0];
              } else {
                const prevNode = oddNodes[index - 1];
                const currNode = oddNodes[index];
                if (transcript - prevNode.number < currNode.number - transcript) {
                  closestNode = prevNode;
                } else {
                  closestNode = currNode;
                }
              }
            }
            setEvenNodes([]);
            setOddNodes([]);
            setNumberStart(closestNode.number);
            speak({ text: "AHORA VAMOS CON EL PUNTO DE DESTINO, DIME EL NOMBRE DE LA CALLE A DONDE QUIERES LLEGAR, DE LO CONTRARIO DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
          } else {
            speak({ text: "DEBES DECIR UN NÚMERO, SI NO QUIERES CONTINUAR DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
          }
      }
    }

    if (listening === false && calculatingPath === true && streetStart !== "" && numberStart !== -1 && streetFinish === "") {
      switch (transcript) {
        case "cancelar":
          speak({
            text: "CANCELANDO EL CÁLCULO DE LA RUTA.", voice: voice, rate: 0.7, pitch: 0.5
          });
          setCalculatingPath(false);
          setStreetStart("");
          setStreetFinish("");
          setNumberStart(-1);
          setNumberFinish(-1);
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          const url = graphUrl + "/graph/nodes-all-street";
          Axios.get(url, {
            headers: {
              Authorization: localStorage.getItem('token'),
              'Content-Type': 'application/json'
            },
            params: {
              street: capitalize(transcript),
            }
          })
            .then((response) => {
              if (response.data.length !== 0) {
                setEvenNodes(response.data.filter(node => node.number % 2 === 0));
                setOddNodes(response.data.filter(node => node.number % 2 !== 0));
                setStreetFinish(capitalize(transcript));
                speak({ text: "DIME EL NÚMERO DE " + transcript + " A DONDE QUIERES LLEGAR, DE LO CONTRARIO DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
              } else {
                speak({ text: "LA CALLE " + transcript + " NO ESTÁ CONTEMPLADA EN EL SISTEMA POR EL MOMENTO. INTENTA CON OTRA CALLE O DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
              }
            })
            .catch((error) => {
              speak({ text: "HA OCURRIDO UN ERROR, INTENTA DE NUEVO EN UNOS MINUTOS.", voice: voice, rate: 0.7, pitch: 0.5 });
              setCalculatingPath(false);
              setStreetStart("");
              setStreetFinish("");
              setNumberStart(-1);
              setNumberFinish(-1);
            });
      }
    }

    if (listening === false && calculatingPath === true && streetStart !== "" && numberStart !== -1 && streetFinish !== "" && numberFinish === -1) {
      switch (transcript) {
        case "cancelar":
          speak({
            text: "CANCELANDO EL CÁLCULO DE LA RUTA.", voice: voice, rate: 0.7, pitch: 0.5
          });
          setCalculatingPath(false);
          setStreetStart("");
          setStreetFinish("");
          setNumberStart(-1);
          setNumberFinish(-1);
          setEvenNodes([]);
          setOddNodes([]);
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          if (!isNaN(transcript)) {
            let closestNode;
            if (transcript % 2 === 0) {
              const index = evenNodes.findIndex(node => node.number >= transcript);
              if (index === 0) {
                closestNode = evenNodes[0];
              } else {
                const prevNode = evenNodes[index - 1];
                const currNode = evenNodes[index];
                if (transcript - prevNode.number < currNode.number - transcript) {
                  closestNode = prevNode;
                } else {
                  closestNode = currNode;
                }
              }
            } else {
              const index = oddNodes.findIndex(node => node.number >= transcript);
              if (index === 0) {
                closestNode = oddNodes[0];
              } else {
                const prevNode = oddNodes[index - 1];
                const currNode = oddNodes[index];
                if (transcript - prevNode.number < currNode.number - transcript) {
                  closestNode = prevNode;
                } else {
                  closestNode = currNode;
                }
              }
            }
            setEvenNodes([]);
            setOddNodes([]);
            setNumberFinish(closestNode.number);
            speak({ text: "CALCULANDO LA RUTA MÁS ACCESIBLE PARA TI.", voice: voice, rate: 0.7, pitch: 0.5 });
            const url = graphUrl + "/graph/vision-shortest-path";
            Axios.get(url, {
              headers: {
                Authorization: localStorage.getItem('token'),
                'Content-Type': 'application/json'
              },
              params: {
                startStreet: streetStart,
                startNumber: numberStart,
                targetStreet: streetFinish,
                targetNumber: closestNode.number
              }
            })
              .then((response) => {
                setPathNodes(response.data.locations);
                setPathInstructions(response.data.instructions);
                setInstructionNumber(0);
                setOnPath(true);
              })
              .catch((error) => {
                speak({ text: "NO SE PUEDE ENCONTRAR LA RUTA, INTENTE CON OTROS PUNTOS DE LLEGADA Y DESTINO.", voice: voice, rate: 0.7, pitch: 0.5 });
                setCalculatingPath(false);
                setStreetStart("");
                setStreetFinish("");
                setNumberStart(-1);
                setNumberFinish(-1);
              });
          } else {
            speak({ text: "DEBES DECIR UN NÚMERO, SI NO QUIERES CONTINUAR DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
          }
      }
    }

    if (listening === false && onPath === true) {
      switch (transcript) {
        case "repetir":
          speak({ text: pathInstructions[instructionNumber], voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        case "cancelar":
          speak({ text: "CANCELANDO LA RUTA CALCULADA", voice: voice, rate: 0.7, pitch: 0.5 });
          setCalculatingPath(false);
          setStreetStart("");
          setStreetFinish("");
          setNumberStart(-1);
          setNumberFinish(-1);
          setPathNodes([]);
          setPathInstructions([]);
          setInstructionNumber(-1);
          setOnPath(false);
          break;
        default:
          speak({ text: "SI QUIERES VOLVER A ESCUCHAR LA ÚLTIMA INSTRUCCIÓN DI 'REPETIR', SI NO QUIERES CONTINUAR CON LA RUTA DI 'CANCELAR'", voice: voice, rate: 0.7, pitch: 0.5 });
      }
    }

    if (listening === false && changingProfile === true) {
      const url = authUserUrl + "/" + localStorage.getItem("userId") + "/profile";
      switch (transcript) {
        case "sin inconvenientes":
          Axios.put(url, "Sin inconvenientes",
            {
              headers: {
                Authorization: localStorage.getItem("token"),
                "Content-Type": "text/plain"
              }
            })
            .then((response) => {
              localStorage.setItem("profile", "Sin inconvenientes");
              speak({
                text: "HAS CAMBIADO TU PERFIL DE ACCESIBILIDAD A 'SIN INCONVENIENTES'", voice: voice, rate: 0.7, pitch: 0.5
              });
              navigate("/home");
            })
            .catch((error) => {
              speak({
                text: "HA OCURRIDO UN ERROR; INTENTE NUEVAMENTE EN UNOS MINUTOS.", voice: voice, rate: 0.7, pitch: 0.5
              });
            });
          setChangingProfile(false);
          break;
        case "inconvenientes de movilidad":
          Axios.put(url, "Inconvenientes de movilidad",
            {
              headers: {
                Authorization: localStorage.getItem("token"),
                "Content-Type": "text/plain"
              }
            })
            .then((response) => {
              localStorage.setItem("profile", "Inconvenientes de movilidad");
              speak({
                text: "HAS CAMBIADO TU PERFIL DE ACCESIBILIDAD A 'INCONVENIENTES DE MOVILIDAD'", voice: voice, rate: 0.7, pitch: 0.5
              });
              navigate("/home");
            })
            .catch((error) => {
              speak({
                text: "HA OCURRIDO UN ERROR; INTENTE NUEVAMENTE EN UNOS MINUTOS.", voice: voice, rate: 0.7, pitch: 0.5
              });
            });
          setChangingProfile(false);
          break;
        case "cancelar":
          speak({
            text: "TU PERFIL SE MANTENDRÁ SIN CAMBIOS.", voice: voice, rate: 0.7, pitch: 0.5
          });
          setChangingProfile(false);
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          speak({
            text: "LO SIENTO, " + transcript + " NO ES UNA OPCIÓN VÁLIDA... " +
              "LAS OPCIONES PARA CAMBIAR TU PERFIL DE ACCESIBILIDAD SON..." +
              "'SIN INCONVENIENTES'... PARA PERSONAS QUE NO TIENEN INCONVENIENTES DE VISIÓN O DE MOVILIDAD... " +
              "'INCONVENIENTES DE MOVILIDAD'... PARA PERSONAS CON INCONVENIENTES DE MOVILIDAD... " +
              "PRESIONA LA PANTALLA Y DI EL PERFIL QUE DESEAS O 'CANCELAR' SI NO QUIERES CAMBIAR TU PERFIL.", voice: voice, rate: 0.7, pitch: 0.5
          });
      }

    }
    if (listening === false && transcript !== "batata" && changingProfile === false && calculatingPath === false) {
      switch (transcript) {
        case "comandos":
          speak({
            text: "ESTOS SON LOS COMANDOS DISPONIBLES: " +
              "'CALCULAR RUTA': PARA CALCULAR LA RUTA MÁS ACCESIBLE PARA TI'..." +
              "'CAMBIAR PERFIL': PARA CAMBIAR TU PERFIL DE ACCESIBILIDAD'..." +
              "CERRAR SESIÓN': PARA CERRAR LA SESION DE TU CUENTA..." +
              "'COMANDOS': PARA ESCUCHAR LA LISTA DE TODOS LOS COMANDOS.", voice: voice, rate: 0.7, pitch: 0.5
          });
          break;
        case "calcular ruta":
          setCalculatingPath(true);
          speak({ text: "DIME EL NOMBRE DE LA CALLE DEL PUNTO DE PARTIDA, DE LO CONTRARIO DI 'CANCELAR'.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        case "cambiar perfil":
          setChangingProfile(true);
          speak({
            text: "LAS OPCIONES PARA CAMBIAR TU PERFIL DE ACCESIBILIDAD SON..." +
              "'SIN INCONVENIENTES'... PARA PERSONAS QUE NO TIENEN INCONVENIENTES DE VISIÓN O DE MOVILIDAD... " +
              "'INCONVENIENTES DE MOVILIDAD'... PARA PERSONAS CON INCONVENIENTES DE MOVILIDAD... " +
              "PRESIONA LA PANTALLA Y DI EL PERFIL QUE DESEAS O 'CANCELAR' SI NO QUIERES CAMBIAR TU PERFIL.", voice: voice, rate: 0.7, pitch: 0.5
          });
          break;
        case "cerrar sesión":
          localStorage.clear();
          navigate("/login");
          break;
        case "":
          speak({ text: "LO SIENTO, NO HE PODIDO ESCUCHARTE BIEN.", voice: voice, rate: 0.7, pitch: 0.5 });
          break;
        default:
          speak({
            text: "LO SIENTO, " + transcript + " NO ES UN COMANDO VÁLIDO. " +
              "ESTOS SON LOS COMANDOS DISPONIBLES: " +
              "'CALCULAR RUTA': PARA CALCULAR LA RUTA MÁS ACCESIBLE PARA TI'..." +
              "'CAMBIAR PERFIL': PARA CAMBIAR TU PERFIL DE ACCESIBILIDAD'..." +
              "CERRAR SESIÓN': PARA CERRAR LA SESION DE TU CUENTA..." +
              "'COMANDOS': PARA ESCUCHAR LA LISTA DE TODOS LOS COMANDOS.", voice: voice, rate: 0.7, pitch: 0.5
          });
      }
    }
  }, [transcript, listening]);

  useEffect(() => {
    if (localStorage.getItem("profile") !== "Inconvenientes de visión") {
      navigate("/home");
    } else {
      const waitForVoices = setInterval(() => {
        const voicesList = window.speechSynthesis.getVoices();
        const spanishVoices = voicesList.filter(voice => voice.lang.includes('es'));
        const pabloVoice = spanishVoices.find(voice => voice.name === "Microsoft Pablo - Spanish (Spain)");
        if (pabloVoice) {
          setVoice(pabloVoice);
          clearInterval(waitForVoices);
        }
      }, 100);
      return () => clearInterval(waitForVoices);
    }
  }, []);

  useEffect(() => {
    if (voice !== undefined) {
      speak({ text: "MANTÉN PRESIONADA LA PANTALLA PARA HABLAR. PRUEBA DECIR LA PALABRA: 'COMANDOS'", voice: voice, rate: 0.7, pitch: 0.5 });
    }
  }, [voice]);

  const handleMouseDown = () => {
    stop();
    cancel();
    setListening(true);
    setTranscript("");
    const audio = new Audio("/sounds/beep.mp3");
    audio.play();
    listen();
  };

  const handleMouseUp = () => {
    stop();
    setListening(false);
    const audio = new Audio("/sounds/beep2.mp3");
    audio.play();
  };

  return (
    <>
      <button className="btn btn-warning rounded-0 btn-mic" onMouseDown={handleMouseDown} onMouseUp={handleMouseUp}>
        <div className="quiet">
          <div className="mb-4"><MicFill className="fill-icon" /></div>
          <h1 className="mt-4">EN ESPERA</h1>
        </div>
        <div className="listening" >
          <div className="mb-4"><Mic className="mic-icon" /></div>
          <h1 className="mt-4">ESCUCHANDO</h1>
          <p className="text-warning transcript">{transcript}</p>
        </div>
      </button>
    </>
  );
}

export default Vision;