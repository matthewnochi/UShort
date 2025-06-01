// src/pages/Home.tsx
import React, { useState } from 'react';
import UrlForm from '../components/UrlForm';
import UrlResult from '../components/UrlResult';

const Home: React.FC = () => {
  const [shortUrl, setShortUrl] = useState('');

  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h1>UShort</h1>
      <UrlForm onShorten={setShortUrl} />
      {shortUrl && <UrlResult shortUrl={shortUrl} />}
    </div>
  );
};

export default Home;
