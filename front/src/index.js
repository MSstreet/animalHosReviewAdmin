import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App'; // 확장자는 생략 가능

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<App />);