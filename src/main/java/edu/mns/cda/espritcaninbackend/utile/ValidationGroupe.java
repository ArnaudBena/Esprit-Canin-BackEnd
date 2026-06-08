package edu.mns.cda.espritcaninbackend.utile;

public interface ValidationGroupe {
    public interface OnUpdate{};
    public interface OnCreate{};
    public interface OnProfilUpdate{}; // self-service adhérent : valide nom/prénom/email/téléphone, PAS le rôle
    public interface OnInscription{};  // inscription publique : nom/prénom/email/password/téléphone, PAS le rôle (forcé Adherent côté serveur)
}
