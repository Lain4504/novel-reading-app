'use client';

import { useState, FormEvent } from 'react';
import { chapterApi } from '@/lib/api';

export default function ChaptersExportPage() {
  const [chapterId, setChapterId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleExport = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const id = chapterId.trim();
    if (!id) {
      setError('Vui lòng nhập Chapter ID');
      return;
    }

    setLoading(true);
    try {
      const blob = await chapterApi.exportPdf(id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `chapter-${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      setSuccess('Tải PDF thành công');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Xuất PDF thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
          Export Chương PDF
        </h1>
        <p style={{ color: 'var(--gray)' }}>
          Tải về file PDF cho nội dung của một chương theo ID (dành cho tất cả người dùng đã đăng nhập)
        </p>
      </div>

      <div className="card">
        <form onSubmit={handleExport}>
          <div className="form-group">
            <label className="form-label" htmlFor="chapterId">
              Chapter ID
            </label>
            <input
              id="chapterId"
              type="text"
              className="form-input"
              placeholder="Nhập Chapter ID (ví dụ: 670f2...)"
              value={chapterId}
              onChange={(e) => setChapterId(e.target.value)}
              disabled={loading}
              required
            />
          </div>

          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          {success && (
            <div style={{ 
              padding: '0.75rem', 
              background: '#10b981', 
              color: 'white', 
              borderRadius: '0.5rem',
              fontSize: '0.875rem',
              marginBottom: '1rem'
            }}>
              {success}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Đang tạo PDF...' : 'Tải về PDF'}
          </button>
        </form>

        <div style={{ 
          marginTop: '1.5rem', 
          padding: '1rem', 
          background: 'var(--light-gray)',
          borderRadius: '0.5rem',
          fontSize: '0.75rem',
          color: 'var(--gray)'
        }}>
          <strong>Mẹo:</strong> Bạn có thể lấy Chapter ID từ trang quản trị hoặc API lấy danh sách chương.
        </div>
      </div>
    </div>
  );
}