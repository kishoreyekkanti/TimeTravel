
class Converter
LATITUDE_TO_KM_MAPPING_PER_DEGREE = {0 => 110.574,
                    15 => 110.649,
                    30 => 110.852,
                    45 => 111.132,
                    60 => 111.412,
                    75 => 111.618,
                    90 => 111.694}

LONGITUDE_TO_KM_MAPPING_PER_DEGREE = {0 => 111.320,
                     15 => 107.551,
                     30 => 96.486,
                     45 => 78.847,
                     60 => 55.800,
                     75 => 28.902,
                     90 => 0.00}
  def self.radius(latitude, longitude, distance)
    km_lat = geo_to_km(latitude.to_i, LATITUDE_TO_KM_MAPPING_PER_DEGREE)
    km_long = geo_to_km(longitude.to_i, LONGITUDE_TO_KM_MAPPING_PER_DEGREE)
    return distance/km_lat.to_f + latitude.to_f, distance/km_long.to_f + longitude.to_f
  end

  private
  
  def self.geo_to_km(geo, geo_mapping)
    case geo
      when 0..14 then geo_mapping[0]
      when 15..30 then geo_mapping[15]
      when 30..45 then geo_mapping[30]
      when 45..60 then geo_mapping[45]
      when 60..75 then geo_mapping[60]
      when 75..90 then geo_mapping[75]
      when 90..100 then geo_mapping[90]
    end
  end
end