require 'rubygems'
require 'sinatra'
require 'dm-core'
require 'dm-timestamps'
require 'dm-migrations'
require 'utils'
require 'active_support'
dir = __FILE__
DataMapper::setup(:default,"sqlite3://#{Dir.pwd}/location.db")

class Location

    include DataMapper::Resource

  property :id,         Serial
  property :latitude,   Decimal
  property :longitude,  Decimal
  property :url,        String
  property :description,String
  property :created_at, DateTime

end

DataMapper.auto_upgrade!


get '/location/index' do
  latitude = params[:latitude]
  longitude = params[:longitude]
  distance = params[:distance]
  staleness = params[:staleness] || 2
  max_latitude, max_longitude = Converter.radius(latitude, longitude, distance ||2)
  locations = Location.all(:conditions => {:created_at => staleness.days.ago..Time.now,
                                           :latitude.gte => latitude.to_f,
                                           :latitude.lte => max_latitude,
                                           :longitude.gte => longitude.to_f,
                                           :longitude.lte => max_longitude})
   p locations.inspect
end

post  '/location/create' do
  location = Location.new()
  location.latitude = params[:latitude]
  location.longitude = params[:longitude]
  location.url = params[:url]
  location.description = params[:description]
  location.created_at = Time.now
  location.save!
end