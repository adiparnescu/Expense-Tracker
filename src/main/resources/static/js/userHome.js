const subcategories = {
    "Groceries": ["Food", "Beverages", "Household Supplies"],
    "Bills": ["Electricity", "Water", "Internet", "Rent"],
    "Entertainment": ["Movies", "Games", "Events"],
    "Transportation": ["Fuel", "Public Transport", "Maintenance"],
    "Healthcare": ["Medicines", "Consultations", "Therapies"],
    "Clothing": ["Casual Wear", "Formal Wear", "Footwear", "Accessories"],
    "Pets": ["Food", "Veterinary", "Grooming", "Toys"],
    "Subscriptions": ["Streaming Services", "Magazines", "Memberships", "Software"],
    "Personal Care": ["Haircuts", "Skincare", "Cosmetics", "Spa Services"],
    "Education": ["Tuition", "Books", "Supplies", "Courses"],
    "Savings and Investments": ["Emergency Fund", "Retirement Savings", "Investments"],
    "Debt Payments": ["Credit Card Payments", "Loan Repayments"],
    "Gifts and Donations": ["Charitable Donations", "Gifts"],
    "Miscellaneous": ["Home Maintenance", "Pet Care", "Membership Fees"],
    "Others": ["Miscellaneous"]
};

document.addEventListener('DOMContentLoaded', function() {
    checkAdminRole();
    fetch('/api/user/transactions/')
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                const transactionsTable = document.getElementById('transactionsTable');
                const transactionsBody = document.getElementById('transactionsBody');
                transactionsBody.innerHTML = '';

                const categoryAmounts = {};
                let totalAmount = 0;

                data.forEach(transaction => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${transaction.category.name}</td>
                        <td>${transaction.subcategory.name}</td>
                        <td>${transaction.amount}</td>
                        <td>${transaction.description}</td>
                        <td>${transaction.date}</td>
                        <td>
                            <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                            <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                        </td>
                    `;
                    transactionsBody.appendChild(row);

                    if (!categoryAmounts[transaction.category.name]) {
                        categoryAmounts[transaction.category.name] = 0;
                    }
                    categoryAmounts[transaction.category.name] += transaction.amount;
                    totalAmount += transaction.amount;
                });

                transactionsTable.style.display = 'table';
                renderChart(categoryAmounts);
                updateTotalAmount(totalAmount);
            } else {
                document.getElementById('noTransactions').style.display = 'block';
            }

            // Populate category options for filter
            const categoryFilterSelect = document.getElementById('categoryFilter');
            for (const category in subcategories) {
                const option = document.createElement('option');
                option.value = category;
                option.textContent = category;
                categoryFilterSelect.appendChild(option);
            }

            categoryFilterSelect.addEventListener('change', handleCategoryChange);

            // Populate subcategory options for filter
            const subcategoryFilterSelect = document.getElementById('subcategoryFilter');
            subcategoryFilterSelect.addEventListener('change', filterTransactions);

            // Add event listener to today expenses button
            const todayExpensesBtn = document.getElementById('todayExpensesBtn');
            todayExpensesBtn.addEventListener('click', filterTodayExpenses);

            // Add event listener to month expenses button
            const monthExpensesBtn = document.getElementById('monthExpensesBtn');
            monthExpensesBtn.addEventListener('click', filterMonthExpenses);

            // Add event listener to specific day filter button
            const specificDayBtn = document.getElementById('specificDayBtn');
            specificDayBtn.addEventListener('click', filterSpecificDayExpenses);

            // Add event listener to specific month filter button
            const specificMonthBtn = document.getElementById('specificMonthBtn');
            specificMonthBtn.addEventListener('click', filterSpecificMonthExpenses);
        })
        .catch(error => {
            console.error('Error fetching transactions:', error);
        });

    // Populate category options for edit modal
    const editCategorySelect = document.getElementById('editCategory');
    for (const category in subcategories) {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        editCategorySelect.appendChild(option);
    }

    document.getElementById('editCategory').addEventListener('change', function() {
        const category = this.value;
        const subcategorySelect = document.getElementById('editSubcategory');
        subcategorySelect.innerHTML = '<option value="">Select Subcategory</option>';
        if (subcategories[category]) {
            subcategories[category].forEach(subcategory => {
                const option = document.createElement('option');
                option.value = subcategory;
                option.textContent = subcategory;
                subcategorySelect.appendChild(option);
            });
        }
    });
});

function checkAdminRole() {
    fetch('/api/user/roles')  // Adjust the endpoint to match your actual API
        .then(response => response.json())
        .then(data => {
            const isAdmin = data.includes('ROLE_ADMIN');
            if (isAdmin) {
                document.getElementById('adminDashboardBtn').style.display = 'inline-block';
            }
        })
        .catch(error => {
            console.error('Error checking admin role:', error);
        });
}

function redirectToAdmin() {
    window.location.href = '/home/admin';
}

function handleCategoryChange() {
    const category = document.getElementById('categoryFilter').value;
    const subcategoryFilterSelect = document.getElementById('subcategoryFilter');
    subcategoryFilterSelect.innerHTML = '<option value="">Select Subcategory</option>';
    if (category && subcategories[category]) {
        subcategories[category].forEach(subcategory => {
            const option = document.createElement('option');
            option.value = subcategory;
            option.textContent = subcategory;
            subcategoryFilterSelect.appendChild(option);
        });
    }
    filterTransactions();
}

function filterTransactions() {
    const category = document.getElementById('categoryFilter').value;
    const subcategory = document.getElementById('subcategoryFilter').value;
    console.log('Filtering by category:', category, 'and subcategory:', subcategory);

    let url = '/api/user/transactions/';
    if (category && subcategory) {
        url = `/api/user/transactions/subcategory?categoryName=${category}&subcategoryName=${subcategory}`;
    } else if (category) {
        url = `/api/user/transactions/category?categoryName=${category}`;
    }

    fetch(url)
        .then(response => response.json())
        .then(data => {
            const transactionsTable = document.getElementById('transactionsTable');
            const transactionsBody = document.getElementById('transactionsBody');
            transactionsBody.innerHTML = '';

            let totalAmount = 0;

            if (data.length > 0) {
                data.forEach(transaction => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${transaction.category.name}</td>
                        <td>${transaction.subcategory.name}</td>
                        <td>${transaction.amount}</td>
                        <td>${transaction.description}</td>
                        <td>${transaction.date}</td>
                        <td>
                            <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                            <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                        </td>
                    `;
                    transactionsBody.appendChild(row);

                    totalAmount += transaction.amount;
                });
                transactionsTable.style.display = 'table';
            } else {
                document.getElementById('noTransactions').style.display = 'block';
            }

            updateTotalAmount(totalAmount);
        })
        .catch(error => {
            console.error('Error fetching filtered transactions:', error);
        });
}

function filterTodayExpenses() {
    const today = new Date().toISOString().split('T')[0];
    fetch(`/api/user/transactions/date?date=${today}`)
        .then(response => response.json())
        .then(data => {
            const transactionsTable = document.getElementById('transactionsTable');
            const transactionsBody = document.getElementById('transactionsBody');
            transactionsBody.innerHTML = '';

            let totalAmount = 0;

            if (data.length > 0) {
                data.forEach(transaction => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${transaction.category.name}</td>
                        <td>${transaction.subcategory.name}</td>
                        <td>${transaction.amount}</td>
                        <td>${transaction.description}</td>
                        <td>${transaction.date}</td>
                        <td>
                            <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                            <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                        </td>
                    `;
                    transactionsBody.appendChild(row);

                    totalAmount += transaction.amount;
                });
                transactionsTable.style.display = 'table';
            } else {
                document.getElementById('noTransactions').style.display = 'block';
            }

            updateTotalAmount(totalAmount);
        })
        .catch(error => {
            console.error('Error fetching today\'s transactions:', error);
        });
}

function filterMonthExpenses() {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
    const endOfMonth = today.toISOString().split('T')[0];
    fetch(`/api/user/transactions/date-period?startOfTimePeriod=${startOfMonth}&endOfTimePeriod=${endOfMonth}`)
        .then(response => response.json())
        .then(data => {
            const transactionsTable = document.getElementById('transactionsTable');
            const transactionsBody = document.getElementById('transactionsBody');
            transactionsBody.innerHTML = '';

            let totalAmount = 0;

            if (data.length > 0) {
                data.forEach(transaction => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${transaction.category.name}</td>
                        <td>${transaction.subcategory.name}</td>
                        <td>${transaction.amount}</td>
                        <td>${transaction.description}</td>
                        <td>${transaction.date}</td>
                        <td>
                            <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                            <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                        </td>
                    `;
                    transactionsBody.appendChild(row);

                    totalAmount += transaction.amount;
                });
                transactionsTable.style.display = 'table';
            } else {
                document.getElementById('noTransactions').style.display = 'block';
            }

            updateTotalAmount(totalAmount);
        })
        .catch(error => {
            console.error('Error fetching current month\'s transactions:', error);
        });
}

function filterSpecificDayExpenses() {
    const specificDay = document.getElementById('specificDayFilter').value;
    if (specificDay) {
        fetch(`/api/user/transactions/date?date=${specificDay}`)
            .then(response => response.json())
            .then(data => {
                const transactionsTable = document.getElementById('transactionsTable');
                const transactionsBody = document.getElementById('transactionsBody');
                transactionsBody.innerHTML = '';

                let totalAmount = 0;

                if (data.length > 0) {
                    data.forEach(transaction => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${transaction.category.name}</td>
                            <td>${transaction.subcategory.name}</td>
                            <td>${transaction.amount}</td>
                            <td>${transaction.description}</td>
                            <td>${transaction.date}</td>
                            <td>
                                <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                                <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                            </td>
                        `;
                        transactionsBody.appendChild(row);

                        totalAmount += transaction.amount;
                    });
                    transactionsTable.style.display = 'table';
                } else {
                    document.getElementById('noTransactions').style.display = 'block';
                }

                updateTotalAmount(totalAmount);
            })
            .catch(error => {
                console.error('Error fetching transactions for specific day:', error);
            });
    }
}

function filterSpecificMonthExpenses() {
    const specificMonth = document.getElementById('specificMonthFilter').value;
    if (specificMonth) {
        const startOfMonth = new Date(specificMonth).toISOString().split('T')[0];
        const endOfMonth = new Date(new Date(specificMonth).getFullYear(), new Date(specificMonth).getMonth() + 1, 0).toISOString().split('T')[0];
        fetch(`/api/user/transactions/date-period?startOfTimePeriod=${startOfMonth}&endOfTimePeriod=${endOfMonth}`)
            .then(response => response.json())
            .then(data => {
                const transactionsTable = document.getElementById('transactionsTable');
                const transactionsBody = document.getElementById('transactionsBody');
                transactionsBody.innerHTML = '';

                let totalAmount = 0;

                if (data.length > 0) {
                    data.forEach(transaction => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${transaction.category.name}</td>
                            <td>${transaction.subcategory.name}</td>
                            <td>${transaction.amount}</td>
                            <td>${transaction.description}</td>
                            <td>${transaction.date}</td>
                            <td>
                                <button class="btn edit-btn" onclick="openEditModal(${transaction.id}, '${transaction.category.name}', '${transaction.subcategory.name}', ${transaction.amount}, '${transaction.description}', '${transaction.date}')">Edit</button>
                                <button class="btn delete-btn" onclick="confirmDelete(${transaction.id})">Delete</button>
                            </td>
                        `;
                        transactionsBody.appendChild(row);

                        totalAmount += transaction.amount;
                    });
                    transactionsTable.style.display = 'table';
                } else {
                    document.getElementById('noTransactions').style.display = 'block';
                }

                updateTotalAmount(totalAmount);
            })
            .catch(error => {
                console.error('Error fetching transactions for specific month:', error);
            });
    }
}

function updateTotalAmount(totalAmount) {
    document.getElementById('totalAmount').textContent = totalAmount.toFixed(2);
}

function sortTable(columnIndex) {
    const table = document.getElementById('transactionsTable');
    const tbody = table.tBodies[0];
    const rows = Array.from(tbody.rows);
    const isAscending = table.dataset.sortOrder === 'asc';

    rows.sort((a, b) => {
        const aText = a.cells[columnIndex].textContent.trim();
        const bText = b.cells[columnIndex].textContent.trim();

        if (!isNaN(aText) && !isNaN(bText)) {
            return isAscending ? aText - bText : bText - aText;
        }

        return isAscending ? aText.localeCompare(bText) : bText.localeCompare(aText);
    });

    table.dataset.sortOrder = isAscending ? 'desc' : 'asc';
    tbody.append(...rows);
}

function openEditModal(id, category, subcategory, amount, description, date) {
    document.getElementById('editCategory').value = category;
    document.getElementById('editSubcategory').innerHTML = '<option value="">Select Subcategory</option>';
    if (subcategories[category]) {
        subcategories[category].forEach(sub => {
            const option = document.createElement('option');
            option.value = sub;
            option.textContent = sub;
            document.getElementById('editSubcategory').appendChild(option);
        });
    }
    document.getElementById('editSubcategory').value = subcategory;
    document.getElementById('editAmount').value = amount;
    document.getElementById('editDescription').value = description;
    document.getElementById('editDate').value = date;
    document.getElementById('editForm').onsubmit = function(event) {
        event.preventDefault();
        updateTransaction(id);
    };
    document.getElementById('editModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function confirmDelete(transactionId) {
    if (confirm('Are you sure you want to delete this transaction?')) {
        deleteTransaction(transactionId);
    }
}

function deleteTransaction(transactionId) {
    fetch(`/api/user/transactions/${transactionId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                alert('Transaction deleted successfully');
                // Refresh the transaction list
                location.reload();
            } else {
                alert('Failed to delete transaction');
            }
        })
        .catch(error => {
            console.error('Error deleting transaction:', error);
            alert('Error deleting transaction');
        });
}

function updateTransaction(transactionId) {
    const form = document.getElementById('editForm');
    const formData = new FormData(form);
    const data = {
        categoryName: formData.get('categoryName'),
        subcategoryName: formData.get('subcategoryName'),
        amount: parseFloat(formData.get('amount')),
        description: formData.get('description'),
        date: formData.get('date')
    };

    fetch(`/api/user/transactions/${transactionId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert('Transaction updated successfully');
                closeModal();
                // Refresh the transaction list
                location.reload();
            } else {
                alert('Failed to update transaction');
            }
        })
        .catch(error => {
            console.error('Error updating transaction:', error);
            alert('Error updating transaction');
        });
}

function renderChart(data) {
    const ctx = document.getElementById('transactionsChart').getContext('2d');
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(data),
            datasets: [{
                label: 'Expenses by Category',
                data: Object.values(data),
                backgroundColor: [
                    '#FF6384',
                    '#36A2EB',
                    '#FFCE56',
                    '#4CAF50',
                    '#FF9800',
                    '#9C27B0',
                    '#E91E63',
                    '#00BCD4',
                    '#8BC34A',
                    '#CDDC39',
                    '#FFC107',
                    '#FF5722',
                    '#795548',
                    '#607D8B'
                ],
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                tooltip: {
                    callbacks: {
                        label: function(tooltipItem) {
                            return `${tooltipItem.label}: $${tooltipItem.raw.toFixed(2)}`;
                        }
                    }
                }
            }
        }
    });
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

