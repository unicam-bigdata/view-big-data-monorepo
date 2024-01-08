import { Canvas } from '@react-three/fiber';
import { Html, Loader } from '@react-three/drei';
import React, {Suspense} from 'react';
import ReactDOM from 'react-dom/client';

import App from './App';
import './index.css';

const Container = (props) => <div className="container" {...props} />

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render (
  <React.StrictMode>
    <Container>
      <Canvas camera={{ position: [200, 200, 200], near: 0.1, far: 60000, fov: 40 }}>
        <ambientLight />
        <pointLight position={[-10, 10, 10]} intensity={1} />
        <Suspense fallback={
          <Html center>
            <Loader />
          </Html>
          }>
          <App />
        </Suspense>
      </Canvas>
    </Container>
  </React.StrictMode>
);

/*const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);*/