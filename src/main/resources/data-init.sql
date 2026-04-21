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
    ('Obéissance',
    'Discipline regroupant les commandes de base du chien.
    - Débutant : Assis, Couché sur commande au calme, à courte distance.
    - Intermédiaire : ajoute Rappel fiable, Marche en laisse sans tirer, Pas bouger court.
    - Confirmé : maîtrise de l''ensemble avec distractions et à distance, ordres à la voix et au geste.'),

    ('Agilité',
    'Discipline sportive de franchissement et de maniabilité.
    - Débutant : Saut obstacle bas, Tunnel droit, Table (arrêt 5s).
    - Intermédiaire : Slalom 6 piquets, Passerelle, Balançoire assistée, Pneu.
    - Confirmé : Parcours complet enchaîné, slalom 12 piquets, gestion du rythme et des refus.'),

    ('Pistage',
    'Travail olfactif et recherche.
    - Débutant : Recherche d''objet familier à courte distance, piste fraîche simple.
    - Intermédiaire : Piste vieillie 15-30 min avec changements de direction, objets intermédiaires.
    - Confirmé : Piste vieillie 1h+, terrain varié, distracteurs olfactifs, identification d''articles.'),

    ('Sociabilisation',
    'Comportement en environnement varié.
    - Débutant : Calme en présence d''autres chiens tenus en laisse, tolère les inconnus à distance.
    - Intermédiaire : Interaction contrôlée avec chiens congénères, urbain animé, transports courts.
    - Confirmé : Comportement neutre en toutes situations, foule, enfants, autres espèces.');