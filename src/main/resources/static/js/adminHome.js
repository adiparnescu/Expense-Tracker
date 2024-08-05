document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('editForm').onsubmit = function(event) {
        event.preventDefault();
        const userId = document.getElementById('editUserId').value;
        updateUser(userId);
    };
});

function getAllUsers() {
    fetch('/api/admin/')
        .then(response => response.json())
        .then(data => {
            const usersTable = document.getElementById('usersTable');
            const usersBody = document.getElementById('usersBody');
            usersBody.innerHTML = '';

            const noUsersMessage = document.getElementById('noUsers');

            if (data.length > 0) {
                noUsersMessage.style.display = 'none';
                data.forEach(user => {
                    const row = document.createElement('tr');
                    const isAdmin = user.roles.some(role => role.name === 'ROLE_ADMIN');
                    row.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>
                            <button class="btn edit-btn" onclick="openEditModal(${user.id}, '${user.username}', '${user.email}', '${user.firstName}', '${user.lastName}')">Edit</button>
                            <button class="btn delete-btn" onclick="deleteUserById(${user.id})">Delete</button>
                            ${isAdmin ? '' : `<button class="btn promote-btn" onclick="makeAdmin(${user.id})">Promote to Admin</button>`}
                        </td>
                    `;
                    usersBody.appendChild(row);
                });
                usersTable.style.display = 'table';
            } else {
                noUsersMessage.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error fetching users:', error);
        });
}

function deleteAllUsers() {
    fetch('/api/admin/', {
        method: 'DELETE',
    })
        .then(response => response.json())
        .then(data => {
            alert(data.response);
            getAllUsers();
        })
        .catch(error => {
            console.error('Error deleting users:', error);
        });
}

function getUserByUsername() {
    const username = document.getElementById('usernameSearch').value;
    fetch(`/api/admin/username?username=${username}`)
        .then(response => response.json())
        .then(data => {
            displaySingleUser(data);
        })
        .catch(error => {
            console.error('Error fetching user by username:', error);
        });
}

function getUserByEmail() {
    const email = document.getElementById('emailSearch').value;
    fetch(`/api/admin/email?email=${email}`)
        .then(response => response.json())
        .then(data => {
            displaySingleUser(data);
        })
        .catch(error => {
            console.error('Error fetching user by email:', error);
        });
}

function displaySingleUser(user) {
    const usersTable = document.getElementById('usersTable');
    const usersBody = document.getElementById('usersBody');
    const noUsersMessage = document.getElementById('noUsers');
    usersBody.innerHTML = '';

    const row = document.createElement('tr');
    const isAdmin = user.roles.some(role => role.name === 'ROLE_ADMIN');
    row.innerHTML = `
        <td>${user.id}</td>
        <td>${user.username}</td>
        <td>${user.email}</td>
        <td>${user.firstName}</td>
        <td>${user.lastName}</td>
        <td>
            <button class="btn edit-btn" onclick="openEditModal(${user.id}, '${user.username}', '${user.email}', '${user.firstName}', '${user.lastName}')">Edit</button>
            <button class="btn delete-btn" onclick="deleteUserById(${user.id})">Delete</button>
            ${isAdmin ? '' : `<button class="btn promote-btn" onclick="makeAdmin(${user.id})">Promote to Admin</button>`}
        </td>
    `;
    usersBody.appendChild(row);
    usersTable.style.display = 'table';
    noUsersMessage.style.display = 'none';
}

function openEditModal(id, username, email, firstName, lastName) {
    document.getElementById('editUserId').value = id;
    document.getElementById('editUsername').value = username;
    document.getElementById('editEmail').value = email;
    document.getElementById('editFirstName').value = firstName;
    document.getElementById('editLastName').value = lastName;
    document.getElementById('editPassword').value = "default";
    document.getElementById('editModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function updateUser(id) {
    const form = document.getElementById('editForm');
    const formData = new FormData(form);
    const data = {
        username: formData.get('username'),
        email: formData.get('email'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        password: formData.get('password')
    };

    fetch(`/api/admin/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            alert('User updated successfully');
            closeModal();
            getAllUsers();
        })
        .catch(error => {
            console.error('Error updating user:', error);
        });
}

function deleteUserById(id) {
    fetch(`/api/admin/${id}`, {
        method: 'DELETE',
    })
        .then(response => response.json())
        .then(data => {
            alert(data.response);
            getAllUsers();
        })
        .catch(error => {
            console.error('Error deleting user:', error);
        });
}

function makeAdmin(id) {
    fetch(`/api/admin/role?id=${id}`, {
        method: 'PUT',
    })
        .then(response => response.json())
        .then(data => {
            alert('User promoted to admin successfully');
            getAllUsers();
        })
        .catch(error => {
            console.error('Error promoting user to admin:', error);
        });
}

function redirectToHome() {
    window.location.href = '/home/user';
}

function logout() {
    fetch('/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).then(() => {
        window.location.href = '/login';
    }).catch(error => {
        console.error('Error logging out:', error);
    });
}
