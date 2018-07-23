##############################################################################
#
#    Copyright 2018 Adobe. All rights reserved.
#    This file is licensed to you under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License. You may obtain a copy
#    of the License at http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software distributed under
#    the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
#    OF ANY KIND, either express or implied. See the License for the specific language
#    governing permissions and limitations under the License.
#
##############################################################################

FROM docker.elastic.co/logstash/logstash:6.2.3

USER root

# Install JRuby
RUN cd /tmp/ && \
    curl -LO "https://repo1.maven.org/maven2/org/jruby/jruby-dist/9.1.17.0/jruby-dist-9.1.17.0-bin.tar.gz" && \
    tar -xzf jruby-dist-9.1.17.0-bin.tar.gz -C / && ls -aslh / && \
    /jruby-9.1.17.0/bin/jruby -v
ENV PATH="${PATH}:/jruby-9.1.17.0/bin"

# Install gem and bundler
RUN yum install -y git gem; yum clean all && \
    /jruby-9.1.17.0/bin/jruby -S gem install bundler

# Install logstash plugin
RUN mkdir -p plugin-dev && cd plugin-dev && git clone https://github.com/herzog31/logstash-input-openwhisk.git
RUN cd plugin-dev/logstash-input-openwhisk && \
    /jruby-9.1.17.0/bin/jruby -S bundle install && \
    /jruby-9.1.17.0/bin/jruby -S gem build logstash-input-openwhisk.gemspec
RUN bin/logstash-plugin install --no-verify /usr/share/logstash/plugin-dev/logstash-input-openwhisk/logstash-input-openwhisk-0.1.2.gem

USER logstash