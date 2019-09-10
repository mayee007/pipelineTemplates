pipeline {
    // Run every 15 minutes, even if there is no commits
    triggers {
        cron('H/15 * * * *')
    } 
    //properties([pipelineTriggers([cron('* * * * *')])])

    environment { 
        gitRepo = 'https://github.com/mayee007/info.git'
        gitBranch = 'master'
        buildCmd = 'mvn clean package -e checkstyle:checkstyle -Dspring.profiles.active=dev'
    }
    agent { node { label 'dockerserver' } }  
    stages {
        stage('checkout') {
            steps {
                // cleanup workspace before each build 
                step([$class: 'WsCleanup'])
                
                // checkout a branch of github repository 
                git branch : "${gitBranch}", url : "${gitRepo}"
                
                // list all files in workspace
                sh 'ls -lrt'
            }
        }
        stage('build') {
            steps {
                echo "Performing build for Build ID ${env.BUILD_ID}"
                sh "${buildCmd}"
            }
        }
        stage('Analysis') {
            steps {
                echo 'Performing Analysis' 
                sh 'hostname'
            }
        }
        stage('Deploy') {

                    when {
                        branch 'dev'    //only run these steps on the master branch
                    }
                    steps {
                        echo 'Dev Deploy' 
                    }

        } // end of deploy stage 
    } // end of stages 
    
    post {
        failure {    // notify speicific users when build fails
            mail(to: 'a@a.com', subject: "Failed Pipeline", body: "Issue with build")
        }
    }
}