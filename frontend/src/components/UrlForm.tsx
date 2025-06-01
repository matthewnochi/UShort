import React, { useState } from 'react';
import { shortenAndMaybeCustomizeUrl } from '../api/urlService';

interface Props {
  onShorten: (shortUrl: string) => void;
}

const UrlForm: React.FC<Props> = ({ onShorten }) => {
  const [originalUrl, setOriginalUrl] = useState('');
  const [customShortCode, setCustomShortCode] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const result = await shortenAndMaybeCustomizeUrl(originalUrl, customShortCode);
      onShorten(result);
      setOriginalUrl('');
      setCustomShortCode('');
    } catch (err) {
      alert((err as Error).message || 'Error shortening URL');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px', margin: '0 auto' }}>
      <input
        type="text"
        placeholder="Enter URL"
        value={originalUrl}
        onChange={(e) => setOriginalUrl(e.target.value)}
        required
      />
      <input
        type="text"
        placeholder="Custom code (optional)"
        value={customShortCode}
        onChange={(e) => setCustomShortCode(e.target.value)}
      />
      <button type="submit">Shorten</button>
    </form>
  );
};

export default UrlForm;
