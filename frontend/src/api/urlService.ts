const API_BASE = 'http://localhost:8080';

export const shortenAndMaybeCustomizeUrl = async (
  originalUrl: string,
  customShortCode?: string
): Promise<string> => {
  const postResponse = await fetch(`${API_BASE}/shorten`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ originalUrl }),
  });

  if (!postResponse.ok) {
    const errorData = await postResponse.json();
    throw new Error(errorData.message);
  }
  
  const shortUrl = await postResponse.text();

  if (customShortCode?.trim()) {
    const putResponse = await fetch(`${API_BASE}/shorten`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ originalUrl, customShortCode }),
    });

    if (!putResponse.ok) {
      const errorData = await putResponse.json();
      throw new Error(errorData.message || 'Failed to update short code');
    }

    return await putResponse.text(); 
  }

  // return the original shortened URL
  return shortUrl;
};
