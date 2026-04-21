package com.example.mydoctor.backend

import com.example.mydoctor.models.Measurement
import com.example.mydoctor.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseManager {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserEmail(): String? = auth.currentUser?.email
    
    // Returns true if user is logged in
    fun isLoggedIn(): Boolean = auth.currentUser != null

    // Retourne null si succès, ou le message d'erreur
    suspend fun login(email: String, pass: String): String? {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            null // succès
        } catch(e: Exception) {
            e.printStackTrace()
            translateFirebaseError(e.message)
        }
    }

    suspend fun signUp(email: String, pass: String, name: String, age: String, sexe: String, antecedents: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            result.user?.let { firebaseUser ->
                val user = User(
                    uid = firebaseUser.uid,
                    name = name,
                    email = email,
                    age = age,
                    sexe = sexe,
                    antecedents = antecedents
                )
                db.collection("users").document(firebaseUser.uid).set(user).await()
            }
            null // succès
        } catch(e: Exception) {
            e.printStackTrace()
            translateFirebaseError(e.message)
        }
    }

    private fun translateFirebaseError(msg: String?): String {
        if (msg == null) return "Erreur inconnue."
        // Affichage du message brut pour diagnostic
        return when {
            msg.contains("email address is already in use", ignoreCase = true) ->
                "Cet email est déjà utilisé. Essayez de vous connecter."
            msg.contains("badly formatted", ignoreCase = true) ||
            msg.contains("invalid email", ignoreCase = true) ->
                "Adresse email invalide."
            msg.contains("password is invalid", ignoreCase = true) ||
            msg.contains("wrong-password", ignoreCase = true) ||
            msg.contains("wrong password", ignoreCase = true) ->
                "Mot de passe incorrect."
            msg.contains("no user record", ignoreCase = true) ||
            msg.contains("user-not-found", ignoreCase = true) ->
                "Aucun compte trouvé avec cet email."
            msg.contains("too-many-requests", ignoreCase = true) ||
            msg.contains("too many requests", ignoreCase = true) ->
                "Trop de tentatives. Attendez quelques minutes."
            msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                "Firebase non configuré. Activez Email/Mot de passe dans Authentication > Sign-in method."
            msg.contains("operation-not-allowed", ignoreCase = true) ||
            msg.contains("not authorized", ignoreCase = true) ->
                "Auth non activée. Allez dans Firebase Console > Authentication > Sign-in method > activez Email/Mot de passe."
            else ->
                // Toujours afficher le vrai message brut
                "Erreur Firebase : $msg"
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserProfile(): User? {
        val uid = getCurrentUserId() ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveMeasurement(type: String, value: String, status: String): Boolean {
        val uid = getCurrentUserId() ?: return false
        val measureId = UUID.randomUUID().toString()
        val measure = Measurement(id = measureId, userId = uid, type = type, resultValue = value, resultStatus = status)
        return try {
            db.collection("measurements").document(measureId).set(measure).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserMeasurements(): List<Measurement> {
        val uid = getCurrentUserId() ?: return emptyList()
        return try {
            val snapshot = db.collection("measurements")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            snapshot.toObjects(Measurement::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
