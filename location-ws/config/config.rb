require 'yaml'
require 'dm-core'
require 'dm-timestamps'
require 'dm-migrations'


configure { @config = YAML.load_file("database.yml")}

configure :development do
  @path = @config["development"]["path"]
  @file = @config["development"]["file"]
end

configure :production do
  @path = @config["production"]["path"]
  @file = @config["production"]["file"]
end

configure {DataMapper::setup(:default, "sqlite3:://#{@path}/#{@file}")}