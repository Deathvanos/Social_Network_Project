document.getElementById("signup-form").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent default form submission

    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const passwordError = document.getElementById("passwordError");

    const email = document.getElementById("email").value;
    const emailError = document.getElementById("emailError");

    const firstName = document.getElementById('firstName').value;
    const firstNameError = document.getElementById("firstNameError");

    const lastName = document.getElementById('lastName').value;
    const lastNameError = document.getElementById("lastNameError");

    const username = document.getElementById('username').value;
    const usernameError = document.getElementById('usernameError');

    let validationError = false;

    // Reset error messages
    passwordError.textContent = "";
    emailError.textContent = "";
    firstNameError.textContent = "";
    lastNameError.textContent = "";
    usernameError.textContent = "";

    if (password === null || password === "") {
        passwordError.textContent = "Le mot de passe ne peut pas être null";
        validationError = true;
    } else if (password !== confirmPassword) {
        passwordError.textContent = "Les mots de passe ne corresponds pas";
        validationError = true;
    }

    if (firstName === null || firstName === "") {
        firstNameError.textContent = "Le prénom ne peut pas être null";
        validationError = true;
    }

    if (lastName === null || lastName === "") {
        lastNameError.textContent = "Le nom ne peut pas être null";
        validationError = true;
    }

    if (username === null || username === "") {
        usernameError.textContent = "Le pseudonym ne peut pas être null";
        validationError = true;
    }

    if (email === null || email === "") {
        emailError.textContent = "L'email ne peut pas être null";
        validationError = true;
    } else if (!validateEmail(email)) {
        emailError.textContent = "Format email invalid";
        validationError = true;
    }

    //if error there don't do any calls to back - don't submit
    if (validationError) { return; }

    // Check email uniqueness
    $.ajax({
        url: "/checkUnique",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({"email": email, "username": username}),
        success: function(response) {
            let uniqueError = false;
            if (response.existingEmail) {
                emailError.textContent = "Cette adresse mail est déjà utilisée";
                uniqueError = true;
            }

            if (response.existingUsername) {
                usernameError.textContent = "Ce pseudonym est déjà utilisé";
                uniqueError = true;
            }

            if (uniqueError) {return;}

            // If all validations pass, submit the form
            showBootstrapAlert("success", "Veuillez confirmer votre email en suivant les instructions envoyer.");
            document.getElementById("signup-form").submit();
        },
        error: function(xhr, status, error) {
            console.error("Error checking uniqueness:", error);
            // Handle error if needed
        }
    });

});

function validateEmail(email) {
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function showBootstrapAlert(type, message) {
    const alertElement = $('<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
        message +
        '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
        '</div>');

    $('#alertContainer').append(alertElement);
}

