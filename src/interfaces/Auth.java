package interfaces;
import User.Patient;
import User.Doctor;
import User.Admin;
import exceptions.UserNotFoundException;
import User.User;

interface Auth {
    User validateCredentials(String email, String password,
                             Patient[] patients, int patientCount,
                             Doctor[] doctors, int doctorCount,
                             Admin[] admins, int adminCount) throws UserNotFoundException;
}