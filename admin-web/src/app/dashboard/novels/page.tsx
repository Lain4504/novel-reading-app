'use client';

import { useState, useEffect } from 'react';
import { FiEdit2, FiTrash2, FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { novelApi } from '@/lib/api';
import { Novel, NovelStatus, Category, NovelUpdateRequest } from '@/types';

export default function NovelsPage() {
  const [novels, setNovels] = useState<Novel[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedNovel, setSelectedNovel] = useState<Novel | null>(null);
  const [error, setError] = useState('');

  // Form state
  const [formData, setFormData] = useState<NovelUpdateRequest>({});

  useEffect(() => {
    loadNovels();
  }, [page]);

  const loadNovels = async () => {
    setLoading(true);
    try {
      const response = await novelApi.getAllNovels(page, 10);
      if (response.success && response.data) {
        setNovels(response.data.content);
        setTotalPages(response.data.totalPages);
      }
    } catch (err) {
      console.error('Error loading novels:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (novel: Novel) => {
    setSelectedNovel(novel);
    setFormData({
      title: novel.title,
      description: novel.description,
      authorName: novel.authorName,
      authorId: novel.authorId,
      categories: novel.categories.join(','),
      status: novel.status,
      isR18: novel.isR18,
      wordCount: novel.wordCount,
      chapterCount: novel.chapterCount,
    });
    setError('');
    setShowModal(true);
  };

  const handleDeleteClick = (novel: Novel) => {
    setSelectedNovel(novel);
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!selectedNovel) return;

    setLoading(true);
    try {
      await novelApi.deleteNovel(selectedNovel.id);
      setShowDeleteModal(false);
      // Show success
      const successDiv = document.createElement('div');
      successDiv.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:1rem;border-radius:0.5rem;z-index:9999;';
      successDiv.textContent = 'Xóa novel thành công!';
      document.body.appendChild(successDiv);
      setTimeout(() => successDiv.remove(), 3000);
      loadNovels();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Xóa novel thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedNovel) return;

    setLoading(true);
    setError('');

    try {
      // Send all form data (backend will handle which fields to update)
      const updateData: NovelUpdateRequest = {
        title: formData.title || selectedNovel.title,
        description: formData.description || selectedNovel.description,
        authorName: formData.authorName || selectedNovel.authorName,
        categories: formData.categories || selectedNovel.categories.join(','),
        status: formData.status || selectedNovel.status,
        isR18: formData.isR18 !== undefined ? formData.isR18 : selectedNovel.isR18,
        wordCount: formData.wordCount !== undefined ? formData.wordCount : selectedNovel.wordCount,
        chapterCount: formData.chapterCount !== undefined ? formData.chapterCount : selectedNovel.chapterCount,
      };

      console.log('Updating novel with data:', updateData);
      await novelApi.updateNovel(selectedNovel.id, updateData);
      
      // Show success
      const successDiv = document.createElement('div');
      successDiv.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:1rem;border-radius:0.5rem;z-index:9999;';
      successDiv.textContent = 'Cập nhật novel thành công!';
      document.body.appendChild(successDiv);
      setTimeout(() => successDiv.remove(), 3000);
      
      setShowModal(false);
      loadNovels();
    } catch (err: any) {
      console.error('Update error:', err);
      setError(err.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status: NovelStatus) => {
    const statusClasses: Record<NovelStatus, string> = {
      [NovelStatus.PUBLISHED]: 'badge-success',
      [NovelStatus.DRAFT]: 'badge-warning',
      [NovelStatus.COMPLETED]: 'badge-info',
      [NovelStatus.HIATUS]: 'badge-secondary',
      [NovelStatus.DROPPED]: 'badge-danger',
    };
    return statusClasses[status] || 'badge-info';
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
          Quản lý Novel
        </h1>
        <p style={{ color: 'var(--gray)' }}>
          Xem và quản lý các tiểu thuyết trong hệ thống
        </p>
      </div>

      <div className="card">
        {loading && novels.length === 0 ? (
          <div className="loading">
            <div className="spinner"></div>
          </div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Cover</th>
                    <th>Tiêu đề</th>
                    <th>Tác giả</th>
                    <th>Thể loại</th>
                    <th>Trạng thái</th>
                    <th>Lượt xem</th>
                    <th>Theo dõi</th>
                    <th>Chapters</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {novels.map((novel) => (
                    <tr key={novel.id}>
                      <td>
                        {novel.coverImage ? (
                          <img
                            src={novel.coverImage}
                            alt={novel.title}
                            style={{
                              width: '50px',
                              height: '70px',
                              objectFit: 'cover',
                              borderRadius: '0.25rem'
                            }}
                          />
                        ) : (
                          <div
                            style={{
                              width: '50px',
                              height: '70px',
                              background: 'var(--light-gray)',
                              borderRadius: '0.25rem',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontSize: '0.75rem',
                              color: 'var(--gray)'
                            }}
                          >
                            No img
                          </div>
                        )}
                      </td>
                      <td>
                        <div style={{ maxWidth: '200px' }}>
                          <strong>{novel.title}</strong>
                          {novel.isR18 && (
                            <span className="badge badge-danger" style={{ marginLeft: '0.5rem', fontSize: '0.625rem' }}>
                              R18
                            </span>
                          )}
                        </div>
                      </td>
                      <td>{novel.authorName}</td>
                      <td>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.25rem', maxWidth: '150px' }}>
                          {novel.categories.slice(0, 2).map((cat) => (
                            <span key={cat} className="badge badge-info" style={{ fontSize: '0.625rem' }}>
                              {cat}
                            </span>
                          ))}
                          {novel.categories.length > 2 && (
                            <span className="badge badge-secondary" style={{ fontSize: '0.625rem' }}>
                              +{novel.categories.length - 2}
                            </span>
                          )}
                        </div>
                      </td>
                      <td>
                        <span className={`badge ${getStatusBadge(novel.status)}`}>
                          {novel.status}
                        </span>
                      </td>
                      <td>{novel.viewCount.toLocaleString()}</td>
                      <td>{novel.followCount.toLocaleString()}</td>
                      <td>{novel.chapterCount}</td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button
                            onClick={() => handleEdit(novel)}
                            className="btn btn-secondary"
                            style={{ padding: '0.375rem 0.75rem' }}
                          >
                            <FiEdit2 size={14} />
                          </button>
                          <button
                            onClick={() => handleDeleteClick(novel)}
                            className="btn btn-danger"
                            style={{ padding: '0.375rem 0.75rem' }}
                          >
                            <FiTrash2 size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div style={{
              marginTop: '1.5rem',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div style={{ color: 'var(--gray)', fontSize: '0.875rem' }}>
                Trang {page + 1} / {totalPages || 1}
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="btn btn-outline"
                >
                  <FiChevronLeft size={16} />
                  Trước
                </button>
                <button
                  onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                  className="btn btn-outline"
                >
                  Sau
                  <FiChevronRight size={16} />
                </button>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Edit Modal */}
      {showModal && selectedNovel && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Chỉnh sửa Novel</h2>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Tiêu đề</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.title || ''}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Mô tả</label>
                  <textarea
                    className="form-textarea"
                    value={formData.description || ''}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    rows={5}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Tên tác giả</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.authorName || ''}
                    onChange={(e) => setFormData({ ...formData, authorName: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Thể loại (phân cách bằng dấu phẩy)</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.categories || ''}
                    onChange={(e) => setFormData({ ...formData, categories: e.target.value })}
                    placeholder="VD: ACTION,ROMANCE,FANTASY"
                  />
                  <small style={{ color: 'var(--gray)', fontSize: '0.75rem' }}>
                    Các thể loại có sẵn: {Object.values(Category).join(', ')}
                  </small>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label className="form-label">Số từ</label>
                    <input
                      type="number"
                      className="form-input"
                      value={formData.wordCount || ''}
                      onChange={(e) => setFormData({ ...formData, wordCount: parseInt(e.target.value) || 0 })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Số chương</label>
                    <input
                      type="number"
                      className="form-input"
                      value={formData.chapterCount || ''}
                      onChange={(e) => setFormData({ ...formData, chapterCount: parseInt(e.target.value) || 0 })}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Trạng thái</label>
                  <select
                    className="form-select"
                    value={formData.status || NovelStatus.DRAFT}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value as NovelStatus })}
                  >
                    {Object.values(NovelStatus).map((status) => (
                      <option key={status} value={status}>{status}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <input
                      type="checkbox"
                      checked={formData.isR18 || false}
                      onChange={(e) => setFormData({ ...formData, isR18: e.target.checked })}
                    />
                    <span className="form-label" style={{ margin: 0 }}>Nội dung R18</span>
                  </label>
                </div>

                {error && <div className="form-error">{error}</div>}
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowModal(false)} className="btn btn-outline">
                  Hủy
                </button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Đang xử lý...' : 'Cập nhật'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && selectedNovel && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '400px' }}>
            <div className="modal-header">
              <h2 className="modal-title">Xác nhận xóa</h2>
            </div>
            <div className="modal-body">
              <p>Bạn có chắc muốn xóa novel <strong>{selectedNovel.title}</strong>?</p>
              <p style={{ color: 'var(--danger)', marginTop: '0.5rem', fontSize: '0.875rem' }}>
                ⚠️ Hành động này không thể hoàn tác!
              </p>
            </div>
            <div className="modal-footer">
              <button type="button" onClick={() => setShowDeleteModal(false)} className="btn btn-outline">
                Hủy
              </button>
              <button onClick={handleDeleteConfirm} className="btn btn-danger" disabled={loading}>
                {loading ? 'Đang xóa...' : 'Xóa'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
