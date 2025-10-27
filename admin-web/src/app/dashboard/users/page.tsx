'use client';

import { useState, useEffect } from 'react';
import { FiSearch, FiEdit2, FiTrash2, FiPlus, FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { userApi } from '@/lib/api';
import { User, UserRole, UserStatus, UserCreateRequest, UserUpdateRequest } from '@/types';

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  // Form state
  const [formData, setFormData] = useState<UserCreateRequest | UserUpdateRequest>({
    username: '',
    email: '',
    password: '',
    roles: [UserRole.USER],
    status: UserStatus.ACTIVE,
  });

  useEffect(() => {
    loadUsers();
  }, [page]);

  useEffect(() => {
    // Filter users based on search query
    if (searchQuery.trim() === '') {
      setFilteredUsers(users);
    } else {
      const query = searchQuery.toLowerCase();
      const filtered = users.filter(user => 
        user.username.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query) ||
        (user.displayName && user.displayName.toLowerCase().includes(query)) ||
        user.id.toLowerCase().includes(query)
      );
      setFilteredUsers(filtered);
    }
  }, [searchQuery, users]);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await userApi.getAllUsers(page, 20);
      if (response.success && response.data) {
        setUsers(response.data.content);
        setFilteredUsers(response.data.content);
        setTotalPages(response.data.totalPages);
      }
    } catch (err) {
      console.error('Error loading users:', err);
      setError('Không thể tải danh sách users');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setModalMode('create');
    setSelectedUser(null);
    setFormData({
      username: '',
      email: '',
      password: '',
      roles: [UserRole.USER],
      status: UserStatus.ACTIVE,
    });
    setError('');
    setShowModal(true);
  };

  const handleEdit = (user: User) => {
    setModalMode('edit');
    setSelectedUser(user);
    setFormData({
      username: user.username,
      email: user.email,
      roles: user.roles,
      status: user.status,
      displayName: user.displayName,
      bio: user.bio,
    });
    setError('');
    setShowModal(true);
  };

  const handleDeleteClick = (user: User) => {
    setSelectedUser(user);
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!selectedUser) return;

    setLoading(true);
    try {
      await userApi.deleteUser(selectedUser.id);
      setShowDeleteModal(false);
      // Show success
      const successDiv = document.createElement('div');
      successDiv.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:1rem;border-radius:0.5rem;z-index:9999;';
      successDiv.textContent = 'Xóa user thành công!';
      document.body.appendChild(successDiv);
      setTimeout(() => successDiv.remove(), 3000);
      loadUsers();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Xóa user thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (modalMode === 'create') {
        await userApi.createUser(formData as UserCreateRequest);
        // Show success
        const successDiv = document.createElement('div');
        successDiv.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:1rem;border-radius:0.5rem;z-index:9999;';
        successDiv.textContent = 'Tạo user thành công!';
        document.body.appendChild(successDiv);
        setTimeout(() => successDiv.remove(), 3000);
      } else if (selectedUser) {
        const updateData = { ...formData };
        if (!updateData.password) {
          delete updateData.password;
        }
        await userApi.updateUser(selectedUser.id, updateData);
        // Show success
        const successDiv = document.createElement('div');
        successDiv.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:1rem;border-radius:0.5rem;z-index:9999;';
        successDiv.textContent = 'Cập nhật user thành công!';
        document.body.appendChild(successDiv);
        setTimeout(() => successDiv.remove(), 3000);
      }
      setShowModal(false);
      loadUsers();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      setLoading(false);
    }
  };

  const handleRoleToggle = (role: UserRole) => {
    const currentRoles = (formData.roles || []) as UserRole[];
    const newRoles = currentRoles.includes(role)
      ? currentRoles.filter(r => r !== role)
      : [...currentRoles, role];
    setFormData({ ...formData, roles: newRoles });
  };

  const getStatusBadge = (status: UserStatus) => {
    const classes: Record<UserStatus, string> = {
      [UserStatus.ACTIVE]: 'badge-success',
      [UserStatus.INACTIVE]: 'badge-warning',
      [UserStatus.BANNED]: 'badge-danger',
      [UserStatus.SUSPENDED]: 'badge-warning'
    };
    return classes[status] || 'badge-info';
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
            Quản lý User
          </h1>
          <p style={{ color: 'var(--gray)' }}>
            Xem và quản lý người dùng trong hệ thống
          </p>
        </div>
        <button onClick={handleCreate} className="btn btn-primary">
          <FiPlus size={16} />
          Tạo User
        </button>
      </div>

      {/* Search Box */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <FiSearch size={18} color="var(--gray)" />
          <input
            type="text"
            className="form-input"
            placeholder="Tìm kiếm theo username, email, display name, ID..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ margin: 0 }}
          />
        </div>
      </div>

      {/* Users Table */}
      <div className="card">
        {loading && users.length === 0 ? (
          <div className="loading">
            <div className="spinner"></div>
          </div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Display Name</th>
                    <th>Roles</th>
                    <th>Status</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredUsers.length === 0 ? (
                    <tr>
                      <td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: 'var(--gray)' }}>
                        {searchQuery ? 'Không tìm thấy user nào' : 'Không có user nào'}
                      </td>
                    </tr>
                  ) : (
                    filteredUsers.map((user) => (
                      <tr key={user.id}>
                        <td>
                          <strong>{user.username}</strong>
                        </td>
                        <td>{user.email}</td>
                        <td>{user.displayName || '-'}</td>
                        <td>
                          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.25rem' }}>
                            {user.roles.map((role) => (
                              <span key={role} className="badge badge-secondary" style={{ fontSize: '0.625rem' }}>
                                {role}
                              </span>
                            ))}
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${getStatusBadge(user.status)}`}>
                            {user.status}
                          </span>
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <button
                              onClick={() => handleEdit(user)}
                              className="btn btn-secondary"
                              style={{ padding: '0.375rem 0.75rem' }}
                            >
                              <FiEdit2 size={14} />
                            </button>
                            <button
                              onClick={() => handleDeleteClick(user)}
                              className="btn btn-danger"
                              style={{ padding: '0.375rem 0.75rem' }}
                            >
                              <FiTrash2 size={14} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
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
                {searchQuery ? 
                  `Hiển thị ${filteredUsers.length} / ${users.length} users (đang tìm kiếm)` :
                  `Trang ${page + 1} / ${totalPages || 1}`
                }
              </div>
              {!searchQuery && (
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
              )}
            </div>
          </>
        )}
      </div>

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">
                {modalMode === 'create' ? 'Tạo User mới' : 'Chỉnh sửa User'}
              </h2>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Username</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.username || ''}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-input"
                    value={formData.email || ''}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">
                    Password {modalMode === 'edit' && '(để trống nếu không đổi)'}
                  </label>
                  <input
                    type="password"
                    className="form-input"
                    value={formData.password || ''}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required={modalMode === 'create'}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Display Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={(formData as any).displayName || ''}
                    onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Bio</label>
                  <textarea
                    className="form-textarea"
                    value={(formData as any).bio || ''}
                    onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Roles</label>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                    {Object.values(UserRole).map((role) => (
                      <label key={role} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <input
                          type="checkbox"
                          checked={(formData.roles || []).includes(role)}
                          onChange={() => handleRoleToggle(role)}
                        />
                        {role}
                      </label>
                    ))}
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select
                    className="form-select"
                    value={formData.status || UserStatus.ACTIVE}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value as UserStatus })}
                  >
                    {Object.values(UserStatus).map((status) => (
                      <option key={status} value={status}>{status}</option>
                    ))}
                  </select>
                </div>

                {error && <div className="form-error">{error}</div>}
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowModal(false)} className="btn btn-outline">
                  Hủy
                </button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Đang xử lý...' : modalMode === 'create' ? 'Tạo' : 'Cập nhật'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && selectedUser && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '400px' }}>
            <div className="modal-header">
              <h2 className="modal-title">Xác nhận xóa</h2>
            </div>
            <div className="modal-body">
              <p>Bạn có chắc muốn xóa user <strong>{selectedUser.username}</strong>?</p>
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
