{{- define "crypto-recommendation-chart.labels" }}
  labels:
    generator: helm
    currentDate: {{ now | htmlDate }}
    version: "{{ $.Chart.AppVersion }}"
{{- end }}
