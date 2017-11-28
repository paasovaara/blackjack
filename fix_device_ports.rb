require 'libusb'

`./scripts/fix_permissions`

def get_dev_path(device_identifier)
  matching_output = `./scripts/list_devices`.split(/\n/).select{ |i| i[device_identifier] }[0]
  if matching_output
    return matching_output.split(' ')[0]
  end
  nil
end

usb = LIBUSB::Context.new

deviceIds = {
  knock_sensor_1: {idVendor: 9025, idProduct: 32833, serial: ""},
  rfid_reader_1: {idVendor: 1027, idProduct: 24577, serial: "AL01EY4M"},

  knock_sensor_2: {idVendor: 6790, idProduct: 29987, serial: ""},
  rfid_reader_2: {idVendor: 1027, idProduct: 24577, serial: "AL01EXHK"}
}


puts "=== Detecting black jack devices..."
# create a map of all connected devices with their corresponding /dev path
connectedDevPaths = {}
deviceIds.each() do |k, v|
  device = usb.devices(idVendor: v[:idVendor], idProduct: v[:idProduct]).first
  if device
    model_id = v[:idProduct].to_s(16).rjust(4, "0")
    vendor_id = v[:idVendor].to_s(16)
    serial_number = v[:serial]
    dev_path = get_dev_path("#{vendor_id} - #{serial_number} - #{model_id}")
    if dev_path
      connectedDevPaths[k] = dev_path
      puts "    #{k.to_s}: #{dev_path}"
    end
  end
end
puts "=== DONE\n\n"

puts "=== Symlinking /dev/tty ports to the correct /dev/ttySxx ports`"

`sudo rm /dev/ttyS33`
`sudo rm /var/lock/LCK..ttyS33`
`sudo rm /dev/ttyS34`
`sudo rm /var/lock/LCK..ttyS34`
`sudo rm /dev/ttyS35`
`sudo rm /var/lock/LCK..ttyS35`
`sudo rm /dev/ttyS36`
`sudo rm /var/lock/LCK..ttyS36`

if connectedDevPaths[:knock_sensor_1]
  `sudo ln -s #{connectedDevPaths[:knock_sensor_1]} /dev/ttyS33`
end

if connectedDevPaths[:knock_sensor_2]
  `sudo ln -s #{connectedDevPaths[:knock_sensor_2]} /dev/ttyS34`
end

if connectedDevPaths[:rfid_reader_1]
  `sudo ln -s #{connectedDevPaths[:knock_sensor_2]} /dev/ttyS35`
end

if connectedDevPaths[:rfid_reader_2]
  `sudo ln -s #{connectedDevPaths[:knock_sensor_2]} /dev/ttyS36`
end

puts "=== DONE\n\n"
