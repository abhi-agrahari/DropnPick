import React, { useState } from 'react';
import axios from 'axios';

export default function HomePage() {
    const [file, setFile] = useState(null);
    const [pin, setPin] = useState("");
    const [downloadedFile, setDownloadedFile] = useState(null);
    const [downloadPin, setDownloadPin] = useState("");

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        const formData = new FormData();
        formData.append("file", file);
        try {
            const res = await axios.post("http://localhost:8080/upload", formData);
            setPin(res.data);
        }
        catch (err) {
            console.error(err);
            alert("Error uploading file");
        }
    }

    const handleDownload = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/download/${downloadPin}`, {
                responseType: "blob",
            });
    
            const disposition = res.headers['content-disposition'];
            let fileName = "downloaded_file";
    
            if (disposition && disposition.includes("filename=")) {
                const matches = disposition.match(/filename="?([^"]+)"?/);
                if (matches?.[1]) {
                    fileName = matches[1];
                }
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
    }           
    
    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-blue-100 flex items-center justify-center px-4 py-10">
            <div className="w-full max-w-3xl bg-white rounded-3xl shadow-2xl p-10 space-y-12 transition-all duration-300">
                <h1 className="text-5xl font-extrabold text-center text-indigo-700 tracking-wide">
                    Drop<span className="text-green-600">-n-</span>Pick
                </h1>
    
                {/* Upload Section */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-5 flex items-center gap-2">
                        📤 Upload File
                    </h2>
                    <label className="block">
                        <input
                            type="file"
                            onChange={handleFileChange}
                            className="block w-full text-sm text-gray-700
                            file:mr-4 file:py-2 file:px-4
                            file:rounded-lg file:border-0
                            file:text-sm file:font-semibold
                            file:bg-indigo-100 file:text-indigo-700
                            hover:file:bg-indigo-200 cursor-pointer"
                        />
                    </label>
                    <button
                        onClick={handleUpload}
                        className="mt-5 w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 rounded-lg transition-all duration-200 shadow-md"
                    >
                        Upload File
                    </button>
                    {pin && (
                        <div className="mt-5 bg-green-100 text-green-700 text-center py-2 rounded-md font-medium border border-green-300">
                            Share this PIN to download: <span className="font-bold">{pin}</span>
                        </div>
                    )}
                </div>
    
                <hr className="border-t border-gray-300" />
    
                {/* Download Section */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-5 flex items-center gap-2">
                        📥 Download File
                    </h2>
                    <input
                        type="text"
                        value={downloadPin}
                        onChange={(e) => setDownloadPin(e.target.value)}
                        placeholder="Enter 6-digit PIN"
                        className="block w-full border border-gray-300 rounded-lg px-4 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-400"
                    />
                    <button
                        onClick={handleDownload}
                        className="mt-5 w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-2.5 rounded-lg transition-all duration-200 shadow-md"
                    >
                        Download File
                    </button>
                </div>
            </div>
        </div>
    );    
}