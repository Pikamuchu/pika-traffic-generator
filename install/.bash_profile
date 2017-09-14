# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

PATH=$PATH:$HOME/bin

export PATH

# Environment variables
export JAVA_HOME=/opt/jdk1.8.0_131
export PATH=$JAVA_HOME/bin:$PATH

export ENTORNO="DEV"

# B2C traffic generator variables
export TRAFFIC_GENERATOR_HOME=/home/b2c/traffic-generator
export PATH=$TRAFFIC_GENERATOR_HOME/bin:$PATH
export TRAFFIC_GENERATOR_PROFILE=dev
export JAVA_OPTS="-Xmx1024m -Xms1024m"

# Spring boot admin variables
export SPRING_BOOT_ADMIN_HOME=/home/b2c/spring-boot-admin
export PATH=$SPRING_BOOT_ADMIN_HOME/bin:$PATH
export SPRING_BOOT_ADMIN_PROFILE=default

# Consul variables
export CONSUL_HOME=/home/b2c/consul
export PATH=$CONSUL_HOME:$PATH

#Arranca ssh-agent ( para que no pida passwords al realizar un ssh )
SSHAGENT=/usr/bin/ssh-agent
SHAGENTARGS="-s"
if [ -z "$SSH_AUTH_SOCK" -a -x "$SSHAGENT" ]; then
    eval `$SSHAGENT $SSHAGENTARGS`
    trap "kill $SSH_AGENT_PID" 0
fi

PS1='[\u@\h \W] \[\033[1;33m\](ATG WEB - ${ENTORNO})\[\033[0;37m\] \$ '
