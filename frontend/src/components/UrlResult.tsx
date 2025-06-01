// src/components/UrlResult.tsx
import React, { useState } from 'react';

interface Props {
  shortUrl: string;
}

const UrlResult: React.FC<Props> = ({ shortUrl }) => {
  const [isCopied, setIsCopied] = useState(false);

  const copyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 3000);
    } catch (err) {
      console.error('Failed to copy: ', err);
    }
  };

  return (
    <div className="url-result">
      <p>Shortened URL:</p>
      <a href={shortUrl} target="_blank" rel="noopener noreferrer" className="short-url-link">
        {shortUrl}
      </a>
      <button onClick={copyToClipboard} className="copy-button">
        {isCopied ? 'Copied!' : 'Copy'}
      </button>
    </div>
  );
};

export default UrlResult;
