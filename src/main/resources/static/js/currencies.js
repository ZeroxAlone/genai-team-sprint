// SAMPLE feature — the front-end half of the Currencies slice.
// Shows both halves of the pattern: READ (fetch the list) and WRITE (POST a new row).
// Copy this file as the template for your feature pages (rates.js, convert.js, ...).

const currencyState = {
  currencies: [],
  filterText: ''
};

function renderCurrencies() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');

  if (currencyState.currencies.length === 0) {
    rows.innerHTML = '<tr><td colspan="3" class="status">No currencies found.</td></tr>';
    status.textContent = '0 currencies loaded from the database.';
    status.classList.remove('err');
    return;
  }

  const filter = currencyState.filterText.trim().toLowerCase();
  const visibleCurrencies = currencyState.currencies.filter(currency => {
    if (!filter) {
      return true;
    }

    return [currency.code, currency.name, currency.symbol ?? '']
      .some(value => value.toLowerCase().includes(filter));
  });

  if (visibleCurrencies.length === 0) {
    rows.innerHTML = '<tr><td colspan="3" class="status">No currencies match your filter.</td></tr>';
  } else {
    rows.innerHTML = visibleCurrencies.map(currency => `
      <tr>
        <td class="mono">${currency.code}</td>
        <td>${currency.name}</td>
        <td class="sym">${currency.symbol ?? ''}</td>
      </tr>`).join('');
  }

  const suffix = filter ? ` Showing ${visibleCurrencies.length} of ${currencyState.currencies.length}.` : '';
  status.textContent = `${currencyState.currencies.length} currencies loaded from the database.${suffix}`;
  status.classList.remove('err');
}

// --- READ: GET /api/currencies and render the table ---
async function loadCurrencies() {
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/currencies');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    currencyState.currencies = await res.json();
    renderCurrencies();
  } catch (err) {
    const rows = document.getElementById('rows');
    rows.innerHTML = '<tr><td colspan="3" class="status err">Could not load currencies.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

function filterCurrencies(event) {
  currencyState.filterText = event.target.value;
  renderCurrencies();
}

// --- WRITE: POST /api/currencies with a JSON body, then re-read the list ---
async function addCurrency(event) {
  event.preventDefault();                       // don't reload the page
  const form = event.target;
  const formStatus = document.getElementById('form-status');
  const body = {
    code: form.code.value.trim().toUpperCase(),
    name: form.name.value.trim(),
    symbol: form.symbol.value.trim()
  };
  try {
    const res = await fetch('/api/currencies', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));   // show the API's 400 message
    }
    formStatus.textContent = `Added ${body.code}.`;
    formStatus.classList.remove('err');
    form.reset();
    loadCurrencies();                            // the write is only "done" once the read shows it
  } catch (err) {
    formStatus.textContent = 'Could not add: ' + err.message;
    formStatus.classList.add('err');
  }
}

loadCurrencies();
document.getElementById('currency-filter').addEventListener('input', filterCurrencies);
document.getElementById('add-form').addEventListener('submit', addCurrency);
