import React from 'react';
import { Button, Divider, Grid, Group, Paper, Select, Stack, TextInput } from '@mantine/core';
import { useParams } from 'react-router-dom';
import { CreateUpdateTitle, DefaultPropertyPanel } from 'components';
import WardConfigs from 'pages/ward/WardConfigs';
import useWardUpdateViewModel from 'pages/ward/WardUpdate.vm';

function WardUpdate() {
  const { id } = useParams();
  const {
    ward,
    form,
    handleFormSubmit,
    provinceSelectList,
  } = useWardUpdateViewModel(Number(id));

  if (!ward) {
    return null;
  }

  return (
    <Stack sx={{ maxWidth: 800 }}>
      <CreateUpdateTitle
        managerPath={WardConfigs.managerPath}
        title={WardConfigs.updateTitle}
      />

      <DefaultPropertyPanel
        id={ward.id}
        createdAt={ward.createdAt}
        updatedAt={ward.updatedAt}
        createdBy="1"
        updatedBy="1"
      />

      <form onSubmit={handleFormSubmit}>
        <Paper shadow="xs">
          <Stack spacing={0}>
            <Grid p="sm">
              <Grid.Col xs={6}>
                <TextInput
                  required
                  label={WardConfigs.properties.name.label}
                  {...form.getInputProps('name')}
                />
              </Grid.Col>
              <Grid.Col xs={6}>
                <TextInput
                  required
                  label={WardConfigs.properties.code.label}
                  {...form.getInputProps('code')}
                />
              </Grid.Col>
              <Grid.Col xs={6}>
                <Select
                  required
                  label={WardConfigs.properties.provinceId.label}
                  placeholder="--"
                  searchable
                  data={provinceSelectList}
                  {...form.getInputProps('provinceId')}
                />
              </Grid.Col>
            </Grid>

            <Divider mt="xs"/>

            <Group position="apart" p="sm">
              <Button variant="default" onClick={form.reset}>Mặc định</Button>
              <Button type="submit">Cập nhật</Button>
            </Group>
          </Stack>
        </Paper>
      </form>
    </Stack>
  );
}

export default WardUpdate;
