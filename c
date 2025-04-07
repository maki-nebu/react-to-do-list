#include <iostream>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/prepared_statement.h>
#include <cppconn/resultset.h>

using namespace std;

const string DB_HOST = "tcp://127.0.0.1:3306";
const string DB_USER = "root";
const string DB_PASS = ""; // Change if your MySQL has a password
const string DB_NAME = "todo_db";

void addTask(sql::Connection* conn) {
    string task;
    cout << "Enter task: ";
    getline(cin, task);

    sql::PreparedStatement* pstmt = conn->prepareStatement("INSERT INTO tasks (task_text) VALUES (?)");
    pstmt->setString(1, task);
    pstmt->execute();
    delete pstmt;

    cout << "Task added!" << endl;
}

void viewTasks(sql::Connection* conn) {
    sql::Statement* stmt = conn->createStatement();
    sql::ResultSet* res = stmt->executeQuery("SELECT * FROM tasks");

    cout << "\nYour Tasks:\n";
    while (res->next()) {
        int id = res->getInt("task_id");
        string text = res->getString("task_text");
        bool done = res->getBoolean("is_completed");

        cout << id << ". " << (done ? "[✔] " : "[ ] ") << text << endl;
    }

    delete res;
    delete stmt;
}

void markCompleted(sql::Connection* conn) {
    int id;
    cout << "Enter task ID to mark as completed: ";
    cin >> id;
    cin.ignore();

    sql::PreparedStatement* pstmt = conn->prepareStatement("UPDATE tasks SET is_completed = TRUE WHERE task_id = ?");
    pstmt->setInt(1, id);
    int updated = pstmt->executeUpdate();
    delete pstmt;

    if (updated)
        cout << "Task marked as completed!" << endl;
    else
        cout << "Task not found." << endl;
}

void deleteTask(sql::Connection* conn) {
    int id;
    cout << "Enter task ID to delete: ";
    cin >> id;
    cin.ignore();

    sql::PreparedStatement* pstmt = conn->prepareStatement("DELETE FROM tasks WHERE task_id = ?");
    pstmt->setInt(1, id);
    int deleted = pstmt->executeUpdate();
    delete pstmt;

    if (deleted)
        cout << "Task deleted." << endl;
    else
        cout << "Task not found." << endl;
}

int main() {
    try {
        sql::Driver* driver = get_driver_instance();
        sql::Connection* conn = driver->connect(DB_HOST, DB_USER, DB_PASS);
        conn->setSchema(DB_NAME);

        while (true) {
            cout << "\n--- To-Do List ---" << endl;
            cout << "1. Add Task\n2. View Tasks\n3. Mark Completed\n4. Delete Task\n5. Exit" << endl;
            cout << "Choose an option: ";
            int choice;
            cin >> choice;
            cin.ignore(); // Clear buffer

            switch (choice) {
                case 1: addTask(conn); break;
                case 2: viewTasks(conn); break;
                case 3: markCompleted(conn); break;
                case 4: deleteTask(conn); break;
                case 5:
                    delete conn;
                    cout << "Goodbye!" << endl;
                    return 0;
                default:
                    cout << "Invalid option." << endl;
            }
        }
    } catch (sql::SQLException &e) {
        cerr << "SQL Error: " << e.what() << endl;
        return 1;
    }
}
