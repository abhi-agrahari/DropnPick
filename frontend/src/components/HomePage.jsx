import React, { useState } from 'react';
import axios from 'axios';

export default function HomePage() {
    const [file, setFile] = useState(null);
    const [pin, setPin] = useState("");
    const [downloadPin, setDownloadPin] = useState("");
    const [maxDownloads, setMaxDownloads] = useState("");
    const [expiryHours, setExpiryHours] = useState("");

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file || !maxDownloads || !expiryHours) {
            alert("Please select file, max downloads, and expiry time");
            return;
        }
        const formData = new FormData();
        formData.append("file", file);
        formData.append("downloadLimit", maxDownloads);
        formData.append("timeLimitHours", expiryHours);

        try {
            const res = await axios.post("http://localhost:8080/files/upload", formData);
            setPin(res.data);
        } catch (err) {
            console.error(err);
            alert("Error uploading file");
        }
    };

    const handleDownload = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/files/download/${downloadPin}`, {
                responseType: "blob",
            });

            const disposition = res.headers['content-disposition'];
            let fileName = "downloaded_file";
            if (disposition && disposition.includes("filename=")) {
                const matches = disposition.match(/filename="?([^"]+)"?/);
                if (matches?.[1]) fileName = matches[1];
            }

            const blob = new Blob([res.data], { type: res.headers['content-type'] });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            alert("Download failed or invalid PIN");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
            <div className="w-full max-w-md bg-white rounded-lg shadow-lg p-6 space-y-6">
                <h1 className="text-3xl font-bold text-center text-indigo-600">Drop-n-Pick</h1>

                {/* Upload Section */}
                <div className="space-y-4">
                    <h2 className="text-lg font-semibold">📤 Upload File</h2>
                    <input type="file" onChange={handleFileChange} className="w-full" />
                    <input
                        type="number"
                        placeholder="Max Downloads"
                        value={maxDownloads}
                        onChange={(e) => setMaxDownloads(e.target.value)}
                        className="w-full border px-3 py-2 rounded"
                    />
                    <input
                        type="number"
                        placeholder="Expiry Time (hours)"
                        value={expiryHours}
                        onChange={(e) => setExpiryHours(e.target.value)}
                        className="w-full border px-3 py-2 rounded"
                    />
                    <button
                        onClick={handleUpload}
                        className="w-full bg-indigo-600 text-white py-2 rounded hover:bg-indigo-700"
                    >
                        Upload
                    </button>
                    {pin && (
                        <p className="text-green-600 text-center">
                            PIN: <span className="font-bold">{pin}</span>
                        </p>
                    )}
                </div>

                <hr />

                {/* Download Section */}
                <div className="space-y-4">
                    <h2 className="text-lg font-semibold">📥 Download File</h2>
                    <input
                        type="text"
                        value={downloadPin}
                        onChange={(e) => setDownloadPin(e.target.value)}
                        placeholder="Enter PIN"
                        className="w-full border px-3 py-2 rounded"
                    />
                    <button
                        onClick={handleDownload}
                        className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
                    >
                        Download
                    </button>
                </div>
            </div>
        </div>
    );
}