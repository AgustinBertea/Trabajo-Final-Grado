import React from "react";
import "../stylesheets/MapPath.css";
import { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from "react-leaflet";
import L from "leaflet";
import iconLocation from "../images/location.png";
import iconCircle from "../images/circle.png";
import iconStart from "../images/start.png";
import iconFinish from "../images/finish.png";
import { graphUrl } from "../services/api-rest";
import Axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSpeechSynthesis } from "react-speech-kit";

function SetViewOnClick({ coords }) {
  const map = useMap();
  map.setView(coords, map.getZoom());

  return null;
}

function MapPath() {
  const navigate = useNavigate();
  const [map, setMap] = useState();
  const [location, setLocation] = useState([-31.4133, -64.1840]);
  const [locationLatitude, setLocationLatitude] = useState(-31.4133);
  const [locationLongitude, setLocationLongitude] = useState(-64.1840);
  const [nodes, setNodes] = useState([]);
  const [startStreet, setStartStreet] = useState("");
  const [startNumber, setStartNumber] = useState("");
  const [finishStreet, setFinishStreet] = useState("");
  const [finishNumber, setFinishNumber] = useState("");
  const [pathNodes, setPathNodes] = useState([]);
  const [pathRoads, setPathRoads] = useState([]);
  const [pathInstructions, setPathInstructions] = useState([]);
  const [calculated, setCalculated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [instructionNumber, setInstructionNumber] = useState(-1);
  const { speak, cancel } = useSpeechSynthesis();
  const [voice, setVoice] = useState();

  useEffect(() => {
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
  }, []);

  useEffect(() => {
    if (instructionNumber !== -1) {
      speak({ text: pathInstructions[instructionNumber], voice: voice, rate: 0.7, pitch: 0.5 });
    }
  }, [instructionNumber]);

  useEffect(() => {
    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setLocation([latitude, longitude]);
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

  const iconUserLocation = new L.icon({
    iconUrl: iconLocation,
    iconSize: [40, 40],
    iconAnchor: [15, 15],
    className: 'rounded-icon'
  });

  useEffect(() => {
    const fetchNodes = () => {
      const url = graphUrl + "/graph/locations-all";
      Axios.get(url)
        .then((response) => {
          setNodes(response.data);
        })
        .catch((error) => {
          console.error(error)
        });
    };
    fetchNodes();
  }, []);

  useEffect(() => {
    setMap(
      <>
        {nodes.length !== 0 ? null : <div className="shadow loading-div"> Cargando... </div>}
        {loading ? <div className="shadow loading-div"> Cargando... </div> : null}
        <MapContainer
          center={[locationLatitude, locationLongitude]}
          zoom={18}
          minZoom={17}
          scrollWheelZoom={true}
          className="map-container map-zoom-default" >
          <TileLayer
            attribution='Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012'
            url='https://{s}.basemaps.cartocdn.com/rastertiles/voyager_labels_under/{z}/{x}/{y}{r}.png'
          />

          {location && <Marker key={localStorage.getItem("userId")} position={location} zIndexOffset={100} icon={iconUserLocation}></Marker>}

          {nodes.length !== 0 && !calculated ? showNodesMarkers() : null}
          {pathNodes.length !== 0 && calculated ? showPathAndInstructions() : null}

          <SetViewOnClick
            coords={[
              locationLatitude,
              locationLongitude
            ]}
          />

        </MapContainer>
      </>
    );
  }, [location, nodes, loading]);

  const showPathAndInstructions = () => {
    const iconOfStart = new L.icon({
      iconUrl: iconStart,
      iconSize: [35, 35],
      iconAnchor: [13, 34]
    });
    const iconOfFinish = new L.icon({
      iconUrl: iconFinish,
      iconSize: [35, 35],
      iconAnchor: [13, 34]
    });

    const nodeStart = <Marker key={pathNodes[0].id} position={[pathNodes[0].latitude, pathNodes[0].longitude]} icon={iconOfStart}></Marker>;
    const nodeFinish = <Marker key={pathNodes[pathNodes.length - 1].id} position={[pathNodes[pathNodes.length - 1].latitude, pathNodes[pathNodes.length - 1].longitude]} icon={iconOfFinish}></Marker>;

    const roadLines = [];
    let weight;
    switch (localStorage.getItem("profile")) {
      case "Sin inconvenientes":
        weight = "weight";
        break;
      case "Inconvenientes de movilidad":
        weight = "motorWeight";
        break;
      case "Inconvenientes de visión":
        weight = "visionWeight";
        break;
    }

    for (let i = 0; i < pathNodes.length - 1; i++) {
      let color;
      if (pathRoads[i].distance === pathRoads[i][weight]) {
        color = { color: "#64c8ff", weight: 8, dashArray: [1, 10], dashOffset: 1 };
      } else if (pathRoads[i].distance > pathRoads[i][weight]) {
        color = { color: "#3cb371", weight: 8, dashArray: [1, 10], dashOffset: 1 };
      } else {
        color = { color: "#ff6347", weight: 8, dashArray: [1, 10], dashOffset: 1 };
      }

      roadLines.push(<Polyline positions={[[pathNodes[i].latitude, pathNodes[i].longitude],
      [pathNodes[i + 1].latitude, pathNodes[i + 1].longitude]]} pathOptions={color} />);
    }

    return ([nodeStart, nodeFinish, roadLines]);
  };

  const showNodesMarkers = () => {
    const iconNode = new L.icon({
      iconUrl: iconCircle,
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });
    return (nodes.map(node => (
      <Marker key={node.id} position={[node.latitude, node.longitude]} icon={iconNode}>
        <Popup>
          <div className="marker-start-end">
            <h5 className="text-center">{node.street} {node.number}</h5>
            <button onClick={() => handleStartClick(node)} className="btn btn-dark mt-1 col-12">Establecer este punto como partida</button>
            <button onClick={() => handleFinishClick(node)} className="btn btn-dark mt-3">Establecer este punto como llegada</button>
          </div>
        </Popup>
      </Marker>
    )));
  };

  const handleStartClick = (node) => {
    setStartStreet(node.street);
    setStartNumber(node.number);
    setErrorMsg("");
  };

  const handleFinishClick = (node) => {
    setFinishStreet(node.street);
    setFinishNumber(node.number);
    setErrorMsg("");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setErrorMsg("");
    if (startStreet.length === 0 || startNumber.length === 0) {
      setErrorMsg("Debes seleccionar un punto de partida.")
    } else if (finishStreet.length === 0 || finishNumber === 0) {
      setErrorMsg("Debes seleccionar un punto de llegada.");
    } else {
      setLoading(true);
      let typeOfShortestPath;
      switch (localStorage.getItem("profile")) {
        case "Sin inconvenientes":
          typeOfShortestPath = "/shortest-path";
          break;
        case "Inconvenientes de movilidad":
          typeOfShortestPath = "/motor-shortest-path";
          break;
        case "Inconvenientes de visión":
          typeOfShortestPath = "/vision-shortest-path";
          break;
        default:
          console.error("Invalid profile");
      }
      const url = graphUrl + "/graph" + typeOfShortestPath;
      Axios.get(url, {
        headers: {
          Authorization: localStorage.getItem('token'),
          'Content-Type': 'application/json'
        },
        params: {
          startStreet: startStreet,
          startNumber: startNumber,
          targetStreet: finishStreet,
          targetNumber: finishNumber
        }
      })
        .then((response) => {
          setPathNodes(response.data.locations);
          setPathRoads(response.data.roads);
          setPathInstructions(response.data.instructions);
          setInstructionNumber(0);
          setCalculated(true);
          setLoading(false);
        })
        .catch((error) => {
          setLoading(false);
          setErrorMsg("Ha ocurrido un error, intente nuevamente en unos minutos.");
        });
    }
  };

  useEffect(() => {
    if (pathNodes.length !== 0) {
      const latitudeGap = Math.abs(Math.abs(pathNodes[instructionNumber].latitude) - Math.abs(locationLatitude));
      const longitudeGap = Math.abs(Math.abs(pathNodes[instructionNumber].longitude) - Math.abs(locationLongitude));
      if (latitudeGap < 0.0001 & longitudeGap < 0.0001) {
        handleNextInstruction();
      }
    }
  }, [location]);

  const handleNextInstruction = () => {
    if (pathInstructions[instructionNumber + 1] === "SKIP") {
      setInstructionNumber(instructionNumber + 2);
    } else {
      setInstructionNumber(instructionNumber + 1);
    }
  };

  const handleFinish = () => {
    speak({ text: "Que tengas un excelente día.", voice: voice, rate: 0.7, pitch: 0.5 });
    navigate("/home");
  };

  const handleRepeat = () => {
    cancel();
    speak({ text: pathInstructions[instructionNumber], voice: voice, rate: 0.7, pitch: 0.5 });
  };

  return (
    <>
      <div className="map-div-container">
        {pathNodes.length === 0 ?
          <form onSubmit={handleSubmit} className="start-end shadow pt-1 p-3 col-xl-6 col-lg-7 col-md-8 col-sm-10 col-12">
            <div className="row align-items-center justify-content-center mt-2">
              <div className="col-sm-9 col-8">
                <input autocomplete="off"
                  disabled
                  type="text"
                  name="startStreet"
                  placeholder="Calle de partida"
                  value={startStreet && startStreet}
                  className="form-control" />
              </div>
              <div className="col-sm-3 col-4">
                <input autocomplete="off"
                  disabled
                  type="text"
                  name="startNumber"
                  placeholder="Número"
                  value={startNumber && startNumber}
                  className="form-control" />
              </div>
            </div>

            <div className="row align-items-center justify-content-center mt-1">
              <div className="col-sm-9 col-8">
                <input autocomplete="off"
                  disabled
                  type="text"
                  name="finishStreet"
                  placeholder="Calle de llegada"
                  value={finishStreet && finishStreet}
                  className="form-control" />
              </div>
              <div className="col-sm-3 col-4">
                <input autocomplete="off"
                  disabled
                  type="text"
                  name="finishNumber"
                  placeholder="Número"
                  value={finishNumber && finishNumber}
                  className="form-control" />
              </div>
            </div>

            {errorMsg && <div className="alert alert-danger mt-2 mb-1">{errorMsg}</div>}

            <div className="row">
              <div className="col-12">
                <button className="btn btn-warning col-12">Calcular la ruta  más accesible</button>
              </div>
            </div>
          </form>
          :
          <div className="start-end shadow pt-1 p-3 col-xl-6 col-lg-7 col-md-8 col-sm-10 col-12">
            <div className="align-items-center justify-content-center mt-2">
              <div className="p-3">
                {pathInstructions[instructionNumber]}
              </div>
              <div className="buttons d-flex justify-content-center">
                {instructionNumber < pathInstructions.length - 1 ?
                  <button onClick={handleRepeat} className="btn btn-warning col-5 right-btn">Repetir instrucción</button>
                  :
                  <button onClick={handleFinish} className="btn btn-dark col-5 right-btn">Finalizar</button>}
              </div>
            </div>
          </div>
        }
        {map}
      </div>
    </>
  );
}

export default MapPath;