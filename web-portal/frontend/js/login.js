document.addEventListener('DOMContentLoaded', () => {
    const btnStudent = document.getElementById('btn-student');
    const btnAdmin = document.getElementById('btn-admin');
    const formStudent = document.getElementById('student-form');
    const formAdmin = document.getElementById('admin-form');

    btnStudent.addEventListener('click', () => {
        // Update Buttons
        btnStudent.classList.add('active');
        btnAdmin.classList.remove('active');
        
        // Update Forms
        formAdmin.classList.remove('active-form');
        setTimeout(() => {
            formAdmin.style.display = 'none';
            formStudent.style.display = 'block';
            setTimeout(() => formStudent.classList.add('active-form'), 10);
        }, 300);
    });

    btnAdmin.addEventListener('click', () => {
        // Update Buttons
        btnAdmin.classList.add('active');
        btnStudent.classList.remove('active');
        
        // Update Forms
        formStudent.classList.remove('active-form');
        setTimeout(() => {
            formStudent.style.display = 'none';
            formAdmin.style.display = 'block';
            setTimeout(() => formAdmin.classList.add('active-form'), 10);
        }, 300);
    });

    // Mock Form submission to redirect for demo
    formStudent.addEventListener('submit', (e) => {
        e.preventDefault();
        window.location.href = 'student.html';
    });

    formAdmin.addEventListener('submit', (e) => {
        e.preventDefault();
        window.location.href = 'admin.html';
    });
});
