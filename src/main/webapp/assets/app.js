/* ecart UI helpers (no inline scripts, so the strict Content-Security-Policy stays on) */
(function () {
  // confirm before dangerous forms:  <form data-confirm="Are you sure?">
  document.addEventListener('submit', function (e) {
    var msg = e.target.getAttribute && e.target.getAttribute('data-confirm');
    if (msg && !window.confirm(msg)) e.preventDefault();
  });

  // auto-submit quantity selects in the cart
  document.querySelectorAll('select[data-autosubmit]').forEach(function (s) {
    s.addEventListener('change', function () { s.form.submit(); });
  });

  // show only the selected payment method's fields at checkout
  var radios = document.querySelectorAll('input[name="method"]');
  function syncPay() {
    var chosen = document.querySelector('input[name="method"]:checked');
    document.querySelectorAll('[data-pay]').forEach(function (box) {
      box.classList.toggle('show', !!chosen && box.getAttribute('data-pay') === chosen.value);
    });
  }
  radios.forEach(function (r) { r.addEventListener('change', syncPay); });
  if (radios.length) syncPay();

  // format card number in groups of 4 and expiry as MM/YY
  var num = document.querySelector('input[name="cardNumber"]');
  if (num) num.addEventListener('input', function () {
    num.value = num.value.replace(/\D/g, '').slice(0, 19).replace(/(.{4})/g, '$1 ').trim();
  });
  var exp = document.querySelector('input[name="cardExpiry"]');
  if (exp) exp.addEventListener('input', function () {
    var v = exp.value.replace(/\D/g, '').slice(0, 4);
    exp.value = v.length > 2 ? v.slice(0, 2) + '/' + v.slice(2) : v;
  });

  // fade out success flash messages
  document.querySelectorAll('.flash.ok').forEach(function (f) {
    setTimeout(function () { f.style.transition = 'opacity .6s'; f.style.opacity = '0'; }, 5000);
  });
})();
