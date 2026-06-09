const API = {
  generateLogin: "/customers/generate_login",
  login: "/customers/login",
  register: "/customers/register",
};

let currentPhone = "";

const views = {
  phone: document.getElementById("view-phone"),
  login: document.getElementById("view-login"),
  register: document.getElementById("view-register"),
  success: document.getElementById("view-success"),
};

const alertEl = document.getElementById("alert");
const stepPhone = document.querySelector('.step[data-step="phone"]');
const stepAuth = document.querySelector('.step[data-step="auth"]');
const stepAuthLabel = document.getElementById("step-auth-label");

function showAlert(message, type = "error") {
  alertEl.textContent = message;
  alertEl.className = `alert ${type}`;
  clearTimeout(showAlert._timer);
  showAlert._timer = setTimeout(() => alertEl.classList.add("hidden"), 5000);
}

function hideAlert() {
  alertEl.classList.add("hidden");
}

function setLoading(button, loading) {
  const text = button.querySelector(".btn-text");
  const spinner = button.querySelector(".spinner");
  button.disabled = loading;
  if (text) text.classList.toggle("hidden", loading);
  if (spinner) spinner.classList.toggle("hidden", !loading);
}

function showView(name) {
  Object.entries(views).forEach(([key, el]) => {
    el.classList.toggle("hidden", key !== name);
    el.classList.toggle("active", key === name);
  });

  stepPhone.classList.toggle("active", name === "phone");
  stepPhone.classList.toggle("done", name !== "phone");
  stepAuth.classList.toggle("active", name === "login" || name === "register");
  stepAuth.classList.toggle("done", name === "success");

  if (name === "login") stepAuthLabel.textContent = "Login";
  else if (name === "register") stepAuthLabel.textContent = "Register";
  else stepAuthLabel.textContent = "Login or Register";
}

function validatePhone(phone) {
  return /^[0-9]{10}$/.test(phone);
}

async function parseResponse(response) {
  const text = await response.text();
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function extractError(data) {
  if (typeof data === "string") return data;
  if (data?.error) return data.error;
  if (data?.errors?.length) return data.errors.join(", ");
  return "Something went wrong. Please try again.";
}

function isUserNotFound(data) {
  const msg = extractError(data).toLowerCase();
  return msg.includes("user not found") || msg.includes("not found");
}

document.getElementById("phone-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert();

  const phone = document.getElementById("phone-input").value.trim();
  if (!validatePhone(phone)) {
    showAlert("Phone number must be exactly 10 digits.");
    return;
  }

  const btn = document.getElementById("phone-submit");
  setLoading(btn, true);

  try {
    const response = await fetch(API.generateLogin, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ PhoneNo: phone }),
    });

    const data = await parseResponse(response);
    currentPhone = phone;

    if (response.ok) {
      document.getElementById("login-phone").value = phone;
      document.getElementById("login-phone-display").textContent = phone;
      document.getElementById("login-password").value = "";
      showView("login");
    } else if (isUserNotFound(data)) {
      document.getElementById("register-phone").value = phone;
      document.getElementById("register-phone-display").textContent = phone;
      showView("register");
    } else {
      showAlert(extractError(data));
    }
  } catch {
    showAlert("Unable to reach the server. Is the backend running?");
  } finally {
    setLoading(btn, false);
  }
});

document.getElementById("login-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert();

  const btn = e.target.querySelector('button[type="submit"]');
  const password = document.getElementById("login-password").value;

  setLoading(btn, true);

  try {
    const response = await fetch(API.login, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ PhoneNo: currentPhone, Password: password }),
    });

    const data = await parseResponse(response);

    if (response.ok) {
      document.getElementById("success-title").textContent = "Welcome back";
      document.getElementById("success-message").textContent =
        typeof data === "string" ? data : "You have signed in successfully.";
      showView("success");
    } else {
      showAlert(extractError(data));
    }
  } catch {
    showAlert("Unable to reach the server.");
  } finally {
    setLoading(btn, false);
  }
});

document.getElementById("register-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert();

  const btn = e.target.querySelector('button[type="submit"]');
  const houseVal = document.getElementById("register-house").value;
  const pincodeVal = document.getElementById("register-pincode").value;

  const payload = {
    Name: document.getElementById("register-name").value.trim(),
    PhoneNo: currentPhone,
    Password: document.getElementById("register-password").value,
    Email: document.getElementById("register-email").value.trim(),
    HouseNo: houseVal ? Number(houseVal) : null,
    Locality: document.getElementById("register-locality").value.trim() || null,
    City: document.getElementById("register-city").value.trim() || null,
    Pincode: pincodeVal ? Number(pincodeVal) : null,
  };

  setLoading(btn, true);

  try {
    const response = await fetch(API.register, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await parseResponse(response);

    if (response.ok) {
      document.getElementById("success-title").textContent = "Account created";
      document.getElementById("success-message").textContent =
        typeof data === "string" ? data : "Registration completed successfully.";
      showView("success");
    } else {
      showAlert(extractError(data));
    }
  } catch {
    showAlert("Unable to reach the server.");
  } finally {
    setLoading(btn, false);
  }
});

function goBackToPhone() {
  currentPhone = "";
  document.getElementById("phone-input").value = "";
  hideAlert();
  showView("phone");
}

document.getElementById("login-back").addEventListener("click", goBackToPhone);
document.getElementById("register-back").addEventListener("click", goBackToPhone);

document.getElementById("success-restart").addEventListener("click", () => {
  document.getElementById("register-form").reset();
  document.getElementById("login-form").reset();
  goBackToPhone();
});

document.getElementById("phone-input").addEventListener("input", (e) => {
  e.target.value = e.target.value.replace(/\D/g, "").slice(0, 10);
});
