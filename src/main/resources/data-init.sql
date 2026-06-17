-- Races
INSERT INTO race (nom) VALUES
    ('Labrador'),
    ('Berger Malinois'),
    ('Caniche'),
    ('Cocker'),
    ('Border Collie'),
    ('Berger Australien'),
    ('Beauceron'),
    ('Golden Retriever'),
    ('Jack Russell'),
    ('Bichon');

-- Roles
INSERT INTO role (nom, description) VALUES
    ('Adherent', 'Utilisateur inscrit au club, peut inscrire ses chiens aux séances'),
    ('Coach', 'Éducateur canin habilité à animer les séances'),
    ('Admin', 'Administrateur du club, gère les utilisateurs et le catalogue de séances et de type de séances');

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

-- Utilisateurs
INSERT INTO utilisateur (nom, prenom, email, password, date_inscription, telephone, id_role) VALUES
    ('Dupont', 'Jean', 'jean.dupont@mail.fr', '$2a$10$eOqxVmckS/wksFSp1axicOHHX9gEWlHuxsYFPBH/TP0Ll3aGpWVP2', '2025-01-15', '0612345678', 1),
    ('Martin', 'Sophie', 'sophie.martin@mail.fr', '$2a$10$HQYGB8q2GRq76YW3L2YSQ.vnTji2OarGA3eodF.sgUWCzRrkDkxXW', '2025-02-03', '0687654321', 1),
    ('Leroy', 'Marie', 'marie.leroy@mail.fr', '$2a$10$OLujrnSpwCGECm1ZuuHxsODT24qq4Tf2GWngbhc4rxqZOAq/79Eem', '2025-02-20', '0623456789', 1),
    ('Moreau', 'Pierre', 'pierre.moreau@mail.fr', '$2a$10$seQeeH7ahH46qhmkO3otgegWzII7J6Lh2tyBSJ9LKnKXWxuMAxX.C', '2025-03-05', '0634567890', 1),
    ('Lefèvre', 'Julie', 'julie.lefevre@mail.fr', '$2a$10$E1i0By8WokcfxI9p0RkoSuvFMhz.PUvXW/H.ljmyAtL5y8K0O0sM6', '2025-03-18', '0645678901', 1),
    ('Girard', 'Nicolas', 'nicolas.girard@mail.fr', '$2a$10$vQTo0YdUjvI.UDfqvcJXDuUx10hfYyazT3C0Y91/Mtduum2cBu2Jy', '2025-04-02', '0656789012', 1),
    ('Roux', 'Émilie', 'emilie.roux@mail.fr', '$2a$10$G4Jdn/iPedU9.D0ffcpLIOr/XSybsCIAc9J7elnwaS5eMKxzyzAc2', '2025-04-15', '0667890123', 1),
    ('Fournier', 'Antoine', 'antoine.fournier@mail.fr', '$2a$10$E6UXpuOBM5letwA1f0xj/OtLfeAnEKDYiNdhAPGkHr6RgnwUokzQG', '2025-05-01', '0678901234', 1),
    ('Mercier', 'Laura', 'laura.mercier@mail.fr', '$2a$10$mKSFLDthnCL8yWST4bRdLejyJTqZOaLemmuQG/cZHeJvJwBj5/dAy', '2025-05-20', '0689012345', 1),
    ('Lambert', 'Hugo', 'hugo.lambert@mail.fr', '$2a$10$gLuSPpukhXPuPDrmAi5bx.YdxxXThw8xA2oxlb6NNk0k5DDb28cpy', '2025-06-01', '0690123456', 1),
    ('Clément', 'Franck', 'franck.clement@mail.fr', '$2a$10$KKr1Xk6lVnMevuNGYc5GU.nCcaLKIdz0AfNhLTDnogidjEPdYV7By', '2025-06-10', '0611223344', 1),
    ('Bernard', 'Lucas', 'lucas.bernard@mail.fr', '$2a$10$bu3VCsRdaFfNYUfLEsXoBuVu4rEICtIgKUMTVdYOoNdx5nMYtG4/q', '2024-11-20', NULL, 2),
    ('Petit', 'Camille', 'camille.petit@mail.fr', '$2a$10$kiQp0BaxB4L79OXBRdQ5ueLQ0IA5cxB5Krh8TsicKgKMVMX20621a', '2024-10-15', '0698765432', 2),
    ('Garcia', 'Thomas', 'thomas.garcia@mail.fr', '$2a$10$bmK6nc0KxRssYdENiQ6aXOgtUF5x5U4ZDp6rdsokCLWO5HqokKnNG', '2025-03-01', NULL, 2),
    ('Benacquista', 'Arnaud', 'arnaud.b@mail.fr', '$2a$10$ECDFVHXc/mDlvxavCQDbC.SOUhZv5D6wWlpzERjnxuk9V5.YBfelC', '2024-09-10', '0654321987', 3);

-- Chiens
INSERT INTO chien (nom, poids, taille, sexe, date_naissance, numero_puce, id_utilisateur, id_race) VALUES
    ('Rex', 32.5, 60.0, 'MALE', '2020-05-12', '250269810000001', 1, 1),
    ('Luna', 28.0, 58.0, 'FEMELLE', '2021-08-30', '250269810000002', 1, 2),
    ('Bella', 6.5, 30.0, 'FEMELLE', '2022-03-15', '250269810000003', 2, 3),
    ('Filou', 13.0, 40.0, 'MALE', '2023-06-10', '250269810000004', 3, 4),
    ('Maya', 18.0, 52.0, 'FEMELLE', '2021-04-20', '250269810000005', 4, 5),
    ('Iko', 12.0, 45.0, 'MALE', '2025-09-05', '250269810000006', 4, 5),
    ('Gribouille', 7.0, 32.0, 'FEMELLE', '2022-11-02', '250269810000007', 5, 3),
    ('Thor', 25.0, 55.0, 'MALE', '2020-10-15', '250269810000008', 6, 6),
    ('Nala', 28.0, 56.0, 'FEMELLE', '2023-02-18', '250269810000009', 7, 8),
    ('Pixel', 4.0, 26.0, 'MALE', '2026-02-10', '250269810000010', 7, 9),
    ('Volt', 35.0, 65.0, 'MALE', '2024-08-12', '250269810000011', 8, 7),
    ('Java', 29.0, 57.0, 'FEMELLE', '2021-12-01', '250269810000012', 9, 1),
    ('Saumon', 5.0, 28.0, 'MALE', '2026-03-20', '250269810000013', 10, 4),
    -- Meute de Franck Clément
    ('Choco', 5.0, 27.0, 'FEMELLE', '2026-03-05', '250269810000014', 11, 4),
    ('Pongo', 18.0, 50.0, 'MALE', '2025-09-10', '250269810000015', 11, 1),
    ('Gaïa', 17.0, 50.0, 'FEMELLE', '2024-12-15', '250269810000016', 11, 5),
    ('Odin', 26.0, 55.0, 'MALE', '2022-04-10', '250269810000017', 11, 6),
    ('Naya', 27.0, 58.0, 'FEMELLE', '2021-05-22', '250269810000018', 11, 2),
    ('Réglisse', 7.0, 33.0, 'MALE', '2023-06-30', '250269810000019', 11, 3),
    ('Tyson', 38.0, 68.0, 'MALE', '2020-02-14', '250269810000020', 11, 7),
    ('Diva', 18.0, 51.0, 'FEMELLE', '2021-03-08', '250269810000021', 11, 5);

-- Types de séance (âges en mois)
INSERT INTO type_seance (libelle, description, age_minimum_mois, age_maximum_mois, duree_minimale, duree_maximale, participants_minimum, participants_maximum) VALUES
    ('Obéissance débutant', 'Commandes de base (assis, couché, pas bouger) en environnement calme.', 0, 360, 30, 60, 2, 8),
    ('Obéissance intermédiaire', 'Rappel fiable, marche en laisse sans tirer, pas bouger prolongé.', 6, 360, 45, 90, 2, 8),
    ('Obéissance confirmé', 'Maîtrise complète avec distractions et à distance, à la voix et au geste.', 12, 360, 45, 90, 2, 6),
    ('Agilité débutant', 'Initiation : saut bas, tunnel droit, table. Bases de la maniabilité.', 12, 120, 45, 60, 3, 4),
    ('Agilité intermédiaire', 'Slalom 6 piquets, passerelle, pneu, balançoire assistée.', 12, 120, 60, 90, 3, 8),
    ('Agilité confirmé', 'Parcours complet enchaîné, slalom 12 piquets, gestion du rythme et des refus.', 18, 120, 60, 90, 2, 6),
    ('Pistage débutant', 'Recherche d''objet familier à courte distance, piste fraîche simple.', 12, 360, 60, 120, 2, 6),
    ('Pistage intermédiaire', 'Piste vieillie 15-30 min avec changements de direction.', 12, 360, 60, 120, 2, 6),
    ('Pistage confirmé', 'Piste vieillie 1h+, terrain varié, distracteurs olfactifs.', 18, 360, 60, 120, 2, 5),
    ('Sociabilisation débutant', 'Calme en présence d''autres chiens en laisse, tolère les inconnus à distance.', 3, 360, 30, 60, 4, 10),
    ('Sociabilisation intermédiaire', 'Interaction contrôlée avec congénères, milieu urbain animé.', 6, 360, 45, 60, 4, 8),
    ('Sociabilisation confirmé', 'Comportement neutre en toutes situations : foule, enfants, autres espèces.', 12, 360, 45, 60, 4, 8),
    ('École du chiot', 'École du chiot pour les chiens de moins de 5 mois (codes canins, éveil).', 0, 5, 30, 45, 2, 6),
    ('Éducation chiot', 'Éducation des chiots de 6 à 12 mois.', 6, 12, 30, 60, 2, 6),
    ('Éducation jeune chien', 'Éducation des jeunes chiens de 1 à 2 ans.', 12, 24, 30, 90, 2, 6),
    ('Éducation chien adulte', 'Éducation des chiens adultes (2 ans et plus).', 24, 360, 30, 120, 2, 6);

-- Séances
INSERT INTO seance (date, heure_debut, duree_minutes, statut, id_type_seance, id_coach) VALUES
    ('2026-02-10', '10:00:00', 60, 'ACTIVE', 1, 12),
    ('2026-03-05', '14:00:00', 60, 'ACTIVE', 4, 13),
    ('2026-04-12', '14:00:00', 90, 'ACTIVE', 5, 13),
    ('2026-03-20', '09:30:00', 90, 'ACTIVE', 7, 14),
    ('2026-05-08', '10:00:00', 45, 'ACTIVE', 10, 12),
    ('2026-04-25', '11:00:00', 45, 'ACTIVE', 1, 14),
    ('2026-05-20', '14:00:00', 90, 'ACTIVE', 6, 13),
    ('2026-06-25', '10:00:00', 60, 'ACTIVE', 4, 12),
    ('2026-07-02', '10:00:00', 30, 'ACTIVE', 13, 13),
    ('2026-07-09', '11:00:00', 45, 'ACTIVE', 14, 14),
    ('2026-07-15', '14:00:00', 45, 'ACTIVE', 1, 14),
    ('2026-08-05', '14:00:00', 90, 'ACTIVE', 5, 13),
    ('2026-09-03', '10:00:00', 60, 'ACTIVE', 15, 14);

-- Inscriptions (chien -> séance)
INSERT INTO inscription (id_chien, id_seance, date_inscription, commentaire_coach, statut_presence, acquisition_validee) VALUES
    -- S1 Obéissance débutant (passée) : Rex & Luna présents, validés
    (1, 1, '2026-01-20', NULL, 'PRESENT', true),
    (2, 1, '2026-01-21', NULL, 'PRESENT', true),
    -- S2 Agilité débutant (passée) : Bella & Maya validées
    (3, 2, '2026-02-15', NULL, 'PRESENT', true),
    (5, 2, '2026-02-16', NULL, 'PRESENT', true),
    -- S3 Agilité intermédiaire (passée) : Bella validée, Maya absente
    (3, 3, '2026-03-20', 'Bella progresse bien sur le slalom', 'PRESENT', true),
    (5, 3, '2026-03-21', NULL, 'ABSENT', false),
    -- S4 Pistage débutant (passée) : Thor validé, Filou encore INSCRIT (à traiter)
    (8, 4, '2026-03-01', NULL, 'PRESENT', true),
    (4, 4, '2026-03-02', NULL, 'INSCRIT', false),
    -- S5 Sociabilisation débutant (passée) : 2 présents validés + 1 ANNULEE
    (7, 5, '2026-04-20', NULL, 'PRESENT', true),
    (8, 5, '2026-04-21', NULL, 'PRESENT', true),
    (9, 5, '2026-04-22', NULL, 'ANNULEE', false),
    -- S6 Obéissance débutant (passée, Franck) : Réglisse présent NON validé (à traiter), Tyson absent
    (19, 6, '2026-04-10', NULL, 'PRESENT', false),
    (20, 6, '2026-04-11', NULL, 'ABSENT', false),
    -- S7 Agilité confirmé (passée, Franck) : Diva présente NON validée -> validation = Agi CONFIRME
    (21, 7, '2026-05-01', 'Prête pour la validation confirmé', 'PRESENT', false),
    -- S8 Agilité débutant (à venir) : COMPLÈTE 4/4
    (20, 8, '2026-06-05', NULL, 'INSCRIT', false),
    (1, 8, '2026-06-06', NULL, 'INSCRIT', false),
    (12, 8, '2026-06-07', NULL, 'INSCRIT', false),
    (4, 8, '2026-06-08', NULL, 'INSCRIT', false),
    -- S9 École du chiot (à venir) : chiots < 5 mois
    (13, 9, '2026-06-10', NULL, 'INSCRIT', false),
    (10, 9, '2026-06-11', NULL, 'INSCRIT', false),
    (14, 9, '2026-06-12', NULL, 'INSCRIT', false),
    -- S10 Éducation chiot (à venir) : 6-12 mois
    (6, 10, '2026-06-10', NULL, 'INSCRIT', false),
    (15, 10, '2026-06-11', NULL, 'INSCRIT', false),
    -- S11 Obéissance débutant (à venir) : Gribouille inscrite, Réglisse ANNULEE
    (7, 11, '2026-06-12', NULL, 'INSCRIT', false),
    (19, 11, '2026-06-09', NULL, 'ANNULEE', false),
    -- S12 Agilité intermédiaire (à venir) : Bella & Gaïa (éligibles via Agilité débutant)
    (3, 12, '2026-06-12', NULL, 'INSCRIT', false),
    (16, 12, '2026-06-12', NULL, 'INSCRIT', false),
    -- S13 Éducation jeune chien (à venir) : Volt
    (11, 13, '2026-06-12', NULL, 'INSCRIT', false);

-- Compétences acquises par chien (état courant)
INSERT INTO chien_competence (id_chien, id_competence, niveau_actuel, date_acquisition) VALUES
    (1, 1, 'DEBUTANT', '2026-02-10'),
    (2, 1, 'DEBUTANT', '2026-02-10'),
    (3, 2, 'INTERMEDIAIRE', '2026-04-12'),
    (5, 2, 'DEBUTANT', '2026-03-05'),
    (8, 3, 'DEBUTANT', '2026-03-20'),
    (8, 4, 'DEBUTANT', '2026-05-08'),
    (7, 4, 'DEBUTANT', '2026-05-08'),
    -- Historique pré-seedé de la meute de Franck
    (17, 1, 'INTERMEDIAIRE', '2026-01-15'),
    (17, 2, 'DEBUTANT', '2026-02-20'),
    (18, 3, 'CONFIRME', '2026-03-10'),
    (16, 2, 'DEBUTANT', '2026-03-01'),
    (21, 2, 'INTERMEDIAIRE', '2026-04-01');

-- Compétences NÉCESSAIRES (prérequis d'entrée) par type - table "necessite"
INSERT INTO type_seance_competence (id_type_seance, id_competence, niveau_minimum_requis) VALUES
    (2, 1, 'DEBUTANT'),
    (3, 1, 'INTERMEDIAIRE'),
    (5, 2, 'DEBUTANT'),
    (6, 2, 'INTERMEDIAIRE'),
    (8, 3, 'DEBUTANT'),
    (9, 3, 'INTERMEDIAIRE'),
    (11, 4, 'DEBUTANT'),
    (12, 4, 'INTERMEDIAIRE');

-- Compétences CONFÉRÉES par type - table "conferer"
INSERT INTO type_seance_competence_conferee (id_type_seance, id_competence, niveau_confere) VALUES
    (1, 1, 'DEBUTANT'),
    (2, 1, 'INTERMEDIAIRE'),
    (3, 1, 'CONFIRME'),
    (4, 2, 'DEBUTANT'),
    (5, 2, 'INTERMEDIAIRE'),
    (6, 2, 'CONFIRME'),
    (7, 3, 'DEBUTANT'),
    (8, 3, 'INTERMEDIAIRE'),
    (9, 3, 'CONFIRME'),
    (10, 4, 'DEBUTANT'),
    (11, 4, 'INTERMEDIAIRE'),
    (12, 4, 'CONFIRME');
