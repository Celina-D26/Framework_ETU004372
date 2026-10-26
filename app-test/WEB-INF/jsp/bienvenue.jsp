<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vue JSP - Framework Sprint 9</title>
    <style>
        :root {
            --primary: #6366f1;
            --bg: #0f172a;
            --card-bg: rgba(30, 41, 59, 0.7);
            --text: #f8fafc;
            --text-muted: #94a3b8;
            --border: rgba(255, 255, 255, 0.1);
        }
        body {
            margin: 0;
            padding: 40px 20px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);
            color: var(--text);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            max-width: 600px;
            width: 100%;
            background: var(--card-bg);
            backdrop-filter: blur(12px);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
        }
        h1 {
            margin-top: 0;
            font-size: 26px;
            color: #a5b4fc;
        }
        .badge {
            display: inline-block;
            background: rgba(99, 102, 241, 0.2);
            color: #818cf8;
            border: 1px solid rgba(99, 102, 241, 0.4);
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 13px;
            margin-bottom: 20px;
        }
        .data-item {
            margin-bottom: 16px;
            background: rgba(15, 23, 42, 0.6);
            padding: 12px 16px;
            border-radius: 8px;
            border-left: 4px solid var(--primary);
        }
        .label {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--text-muted);
            margin-bottom: 4px;
        }
        .value {
            font-size: 16px;
            font-weight: 500;
        }
        .btn {
            display: inline-block;
            margin-top: 20px;
            background: var(--primary);
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.2s ease;
        }
        .btn:hover {
            background: #4f46e5;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <div class="container">
        <span class="badge">ModelAndView (Sprint 7)</span>
        <h1>Page de Vue JSP</h1>
        <p>Cette page a été générée via le moteur de vue <code>ModelAndView</code> du framework.</p>

        <div class="data-item">
            <div class="label">Nom de l'utilisateur</div>
            <div class="value">${nom}</div>
        </div>

        <div class="data-item">
            <div class="label">Message du Contrôleur</div>
            <div class="value">${message}</div>
        </div>

        <div class="data-item">
            <div class="label">Horodatage</div>
            <div class="value">${date}</div>
        </div>

        <a href="index.html" class="btn">← Retour au Dashboard</a>
    </div>
</body>
</html>
