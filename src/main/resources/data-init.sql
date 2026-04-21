-- Races
INSERT INTO race (nom) VALUES
    ('Labrador'),
    ('Berger Malinois'),
    ('Caniche');

-- Roles
INSERT INTO role (nom, description) VALUES
    ('ADHERENT', 'Utilisateur inscrit au club, peut inscrire ses chiens aux séances'),
    ('COACH', 'Éducateur canin habilité à animer les séances'),
    ('ADMIN', 'Administrateur du club, gère les utilisateurs et le catalogue');

-- Compétences
INSERT INTO competence (nom, description) VALUES
    ('Assis', 'Le chien s''assoit sur commande et maintient la position'),
    ('Rappel', 'Le chien revient immédiatement vers son maître à l''appel'),
    ('Marche en laisse', 'Le chien marche calmement en laisse sans tirer'),
    ('Pas bouger', 'Le chien reste immobile malgré les distractions');