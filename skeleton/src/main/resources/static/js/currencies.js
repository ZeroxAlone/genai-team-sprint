// SAMPLE feature — the front-end half of the Currencies slice.
// Fetches the REST endpoint and renders the rows. Plain fetch + DOM, no framework.
// Copy this file as the template for your feature pages (rates.js, convert.js, ...).

async function loadCurrencies() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/currencies');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const currencies = await res.json();

    if (currencies.length === 0) {
      rows.innerHTML = '<tr><td colspan="3" class="status">No currencies found.</td></tr>';
      return;
    }

    rows.innerHTML = currencies.map(c => `
      <tr>
        <td class="mono">${c.code}</td>
        <td>${c.name}</td>
        <td class="sym">${c.symbol ?? ''}</td>
      </tr>`).join('');
    status.textContent = `${currencies.length} currencies loaded from the database.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="3" class="status err">Could not load currencies.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

loadCurrencies();
