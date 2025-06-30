for port in {50051..50060}; do
	pid=$(lsof -t -i:$port)
	if [ -n "$pid" ]; then
		echo "Killing process $pid on port $port"
		kill -9 $pid
	fi
done
