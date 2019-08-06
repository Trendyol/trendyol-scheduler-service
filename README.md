# Trendyol Scheduler Service

![Trendyol Scheduler Service](https://raw.githubusercontent.com/Trendyol/trendyol-scheduler-service/master/src/main/resources/scheduler-service-icon.png)

- Easily scheduled jobs for micro service architecture APIs.

## Setup And Run

- To run scheduler-service at your local environment, please run the commands below;

```docker-compose up -d```

- After the containers started successfully, you can run Scheduler Service.

## Job Types

- There are two main job types. They are Scheduled Jobs and Future Tasks.

### Scheduled Jobs

- Scheduled Jobs are used for triggering a task in definite time periods using cron notation. There are two types of Scheduled Jobs: Bean Scheduled Jobs and Rest Scheduled Jobs.

#### _Bean Scheduled Jobs_

- Bean scheduled jobs trigger a bean inside a micro service. Check-out the Scheduler Controller. This job triggers this endpoint in the destination service and activates the given bean's method.

#### _Rest Scheduled Jobs_

- Rest scheduled jobs trigger an endpoint at a micro service. The endpoint is defined as url and path in the job definition.

### Future Tasks

- Future Tasks' aim is to trigger a task in the future for once. This uses only rest endpoints. The needed rest endpoint is defined with url and path in the job definition.

## Usage

- To activate scheduled jobs and future tasks, first you must insert jobs by type into the DB. Jobs have cron expression fields to set the firing times. Please check out the change set file '1.0.5.xml' and controller 'SampleJobController' (rest scheduled job), controller 'SampleFutureTaskController' (future task) and also service 'SampleJobService' (bean scheduled job) for examples.

- In addition, Future Tasks can be created by sending a message to the rabbit exchange 'trendyol.scheduler.future.job'. Please checkout the FutureJobRequest for the needed fields for the message.

- If you want to activate bean scheduled jobs at your custom apis, you can migrate the 'SchedulerController' to the destination app. After that, the '/scheduler/invoke' endpoint will be ready to trigger bean methods.

## Contribution

- Feel free to contribute to the Trendyol Scheduler Service.