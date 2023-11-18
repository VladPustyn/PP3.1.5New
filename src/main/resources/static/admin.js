//получение имени пользователя и его ролей
function fetchUserInfo() {
    fetch('admin/role') // Запрос информации о ролях пользователя
        .then(response => response.json())
        .then(data => {
            const navbar = document.querySelector('.navbar');
            if (data && data.username) {
                let roles = "";
                if (data.admin === "ADMIN") {
                    roles += "ADMIN ";
                }
                if (data.user === "USER") {
                    roles += "USER ";
                }

                const userInfoElement = document.createElement('h3');
                userInfoElement.innerHTML = `<span class="badge badge-dark">${data.username} with roles: ${roles}</span>`;

                const firstChild = navbar.firstElementChild;

                // Вставляем новый элемент перед первым элементом .navbar
                navbar.insertBefore(userInfoElement, firstChild);
            }
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
        });
}

// Вызов функции fetchUserInfo для получения данных о пользователе при загрузке страницы
document.addEventListener('DOMContentLoaded', fetchUserInfo);


// Отправка запроса и заполнение таблицы при загрузке страницы
document.addEventListener('DOMContentLoaded', function () {
    fillUserTable();
});

function fillUserTable() {
    fetch('/admin/users')
        .then(response => response.json())
        .then(data => {
            const userTableBody = document.getElementById('userTableBody');
            userTableBody.innerHTML = '';

            data.forEach(user => {
                const roles = user.roles.map(role => role.name).join(', '); // Получаем строку с ролями пользователя

                // Создаем строку таблицы для каждого пользователя
                const row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.age}</td>
                        <td>${user.username}</td>
                        <td>${user.phoneNumber}</td>
                        <td>${roles}</td>
                           <td>
                            <button class="btn btn-primary btn-md" onclick="openEditModal(${user.id})">Edit</button>
                        </td>
                        <td>
                            <button class="btn btn-danger btn-md" onclick="openDeleteModal(${user.id})">Delete</button>
                        </td>
                    </tr>
                `;

                // Добавляем созданную строку в тело таблицы
                userTableBody.innerHTML += row;
            });
        })
        .catch(error => {
            console.error('Error fetching users:', error);
        });
}

// New User
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('formAdd');

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const selectedRoles = Array.from(form.selectedRoles.selectedOptions).map(option => parseInt(option.value));

        const userData = {
            username: form.username.value,
            password: form.password.value,
            age: parseInt(form.age.value),
            phoneNumber: form.phoneNumber.value,
            roles: selectedRoles
        };

        try {
            const response = await fetch('admin/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });


            if (response.ok) {
                console.log('User added successfully!');
                fillUserTable(); // Обновление таблицы пользователей
                form.reset(); // Сброс формы
                const userTableTab = document.querySelector('a[href="#allUser"]');
                userTableTab.click();
            } else {
                console.error('Failed to add user.');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });
});




// Переменная для хранения userId глобально
let currentUserId = null;

function openDeleteModal(userId) {
    // Получение данных пользователя по userId
    fetch(`/admin/userInfo/${userId}`)
        .then(response => response.json())
        .then(user => {
            // Заполнение данных модального окна
            document.getElementById('username').value = user.username;
            document.getElementById('age').value = user.age;
            document.getElementById('phoneNumber').value = user.phoneNumber;

            const rolesSelect = document.getElementById('roles');
            rolesSelect.innerHTML = '';
            user.roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.textContent = role.name;
                rolesSelect.appendChild(option);
            });

            // Сохранение userId в глобальную переменную
            currentUserId = userId;

            $('#deleteModal').modal('show'); // Показываем модальное окно
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });
}

// удаление пользователя после подтверждения
function confirmDelete() {
    if (currentUserId) {
        // Отправка DELETE запроса для удаления пользователя
        fetch(`/admin/delete/${currentUserId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    console.log('User deleted successfully!');
                    $('#deleteModal').modal('hide');
                    fillUserTable(); // Например, вызов функции для обновления таблицы
                } else {
                    console.error('Failed to delete user.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    } else {
        console.error('User ID is missing.');
    }
}

function cancelDelete() {
    // Скрываем модальное окно при отмене операции
    $('#deleteModal').modal('hide');
    // Сбрасываем текущий ID пользователя
    currentUserId = null;
}

// глобальная переменная

let editUserId = null;

//  открытие модального окна на изменение данных
function openEditModal(userId) {
    editUserId = userId;
    fetch(`admin/userInfo/${userId}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('username1').value = user.username;
            document.getElementById('age1').value = user.age;
            document.getElementById('phoneNumber1').value = user.phoneNumber;


            const rolesSelect = document.getElementById('roles1');
            rolesSelect.innerHTML = '';


            const userOption = document.createElement('option');
            userOption.value = '2';
            userOption.textContent = 'USER';
            rolesSelect.appendChild(userOption);

            const adminOption = document.createElement('option');
            adminOption.value = '1';
            adminOption.textContent = 'ADMIN';
            rolesSelect.appendChild(adminOption);

            console.log(userOption);
            console.log(adminOption);

            // Если у пользователя есть роли, устанавливаем их в списке
            user.roles.forEach(userRole => {
                Array.from(rolesSelect.options).forEach(option => {
                    if (option.value === userRole) {
                        option.selected = true;
                    }
                });
            });

            $('#editModal').modal('show');
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
        });
}

document.getElementById('updateButton').addEventListener('click', function (event) {
    event.preventDefault();

    // Получение значений из формы
    const username = document.getElementById('username1').value;
    const password = document.getElementById('password1').value;
    const age = parseInt(document.getElementById('age1').value);
    const phoneNumber = document.getElementById('phoneNumber1').value;

    // Получение выбранных ролей из формы
    const selectedRoles = Array.from(document.querySelectorAll('#roles1 option:checked')).map(option => option.value);


    // Формирование объекта с обновленными данными
    const updatedUser = {
        id: editUserId,
        username: username,
        password: password,
        age: age,
        phoneNumber: phoneNumber,
        roles: selectedRoles
    };

    // Отправка  данных PATCH
    fetch(`admin/update/${editUserId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedUser),
    })
        .then(response => {
            if (response.ok) {
                console.log('User info updated successfully!');
                fillUserTable();
            } else {
                console.error('Failed to update user info.');
            }
        })
        .catch(error => {
            console.error('Error updating user info:', error);
        });

    // Закрытие модального окна
    $('#editModal').modal('hide');

});


document.getElementById('userLink').addEventListener('click', function(event) {
    event.preventDefault();

    fetch('admin/about')
        .then(response => response.text())
        .then(data => {
            const adminPageContainer = document.getElementById('bigContainer');
            adminPageContainer.innerHTML = data;

            fetch('admin/roles') // Получаем роли пользователя
                .then(response => response.json())
                .then(roleNames => {
                    const navbar = document.getElementById('navbarAdminUser');

                    fetch('admin/info') // Получаем информацию о пользователе
                        .then(response => response.json())
                        .then(person => {
                            const username = person.username; // Получаем имя пользователя


                            const userInfoElement = document.createElement('h3');
                            userInfoElement.innerHTML = `<span class="badge badge-dark">${username} with roles: ${roleNames.join(' ')}</span>`;

                            const firstChild = navbar.firstElementChild;

                            navbar.insertBefore(userInfoElement, firstChild);
                        })
                        .catch((error) => {
                            console.error('Error:', error);
                        });
                })
                .catch((error) => {
                    console.error('Error:', error);
                });

            fetch('admin/info')
                .then(response => response.json())
                .then(person => {
                    let table = document.querySelector('.table-container table tbody');

                    let roles = person.roles.map(role => role.name).join(", ");

                    let row = `
                        <tr>
                            <td>${person.id}</td>
                            <td>${person.age}</td>
                            <td>${person.username}</td>
                            <td>${person.phoneNumber}</td>
                            <td>${roles}</td>
                        </tr>
                    `;

                    table.insertAdjacentHTML('beforeend', row);
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        })
        .catch(error => {
            console.error('Error fetching content:', error);
        });
});