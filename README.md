# AfraScan - Votre Assistant Médical Intelligent 🩺🤖

**AfraScan** est une application mobile Android innovante conçue pour offrir une orientation médicale rapide et accessible. Grâce à l'IA avancée de Google (Gemini 1.5 Flash), AfraScan permet aux utilisateurs de discuter de leurs symptômes via une interface hybride unique (Vocale et Texte).

## 🚀 Fonctionnalités Clés

### 🎙️ Assistant Vocal Intuitif (Dr. AfraScan)
- Interface de chat hybride : Parlez naturellement ou tapez vos messages.
- Détection automatique de silence pour une conversation fluide.
- Synthèse vocale (TTS) pour une interaction naturelle et accessible.

### 🏥 Expertise Médicale IA
- **Analyse de symptômes** : Dr. AfraScan mène une enquête structurée (intensité, durée, localisation).
- **Conseils de traitement** : Suggestions de remèdes de premier secours et de médicaments courants (sans ordonnance).
- **Triage d'Urgence** : Détection automatique des signes de gravité et orientation immédiate vers l'hôpital.

### 🌍 Mode "Cerveau de Secours" (Hors-ligne)
- Logiciel conçu pour fonctionner même sans connexion internet ou clé API.
- Système de consultation locale robuste pour collecter les symptômes en toutes circonstances.

### 📱 Modules de Santé intégrés
- **Analyse de Toux** : Dépistage intelligent par analyse sonore.
- **Score de Risque Santé** : Calcul d'un indicateur de santé global basé sur vos mesures.
- **Authentification Sécurisée** : Connexion sécurisée via Firebase.

---

## 🛠️ Installation & Configuration

### Prérequis
- Android Studio Ladybug ou version ultérieure.
- Un projet Firebase avec le fichier `google-services.json` placé dans le dossier `app/`.

### Configuration de l'IA (Gemini)
Pour activer l'intelligence complète :
1. Obtenez une clé API sur le [Google AI Studio](https://aistudio.google.com/).
2. Ouvrez le fichier `app/src/main/java/com/example/mydoctor/VoiceAiScreen.kt`.
3. Remplacez la constante `GEMINI_API_KEY` (ligne 57) par votre clé.

```kotlin
private const val GEMINI_API_KEY = "VOTRE_CLE_ICI"
```

## 🏗️ Stack Technique
- **Langage** : Kotlin
- **UI** : Jetpack Compose (Modern & Premium Design)
- **IA** : Google Gemini 1.5 Flash (via API HTTP Native)
- **Backend** : Firebase (Auth, Firestore)
- **Services Android** : SpeechRecognizer, TextToSpeech

---

## ⚠️ Avertissement Médical (Disclaimer)
AfraScan est un assistant d'orientation et d'information. **Il ne remplace pas un diagnostic médical professionnel.** En cas de douleur thoracique, difficulté respiratoire ou toute autre urgence vitale, contactez immédiatement les services de secours (15, 112 ou votre numéro local).

---
*Développé avec passion pour l'innovation médicale.*